package com.orderservice.service;

import com.orderservice.dto.CartItemDTO;
import com.orderservice.dto.OrderDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Order;
import com.orderservice.model.OrderDetail;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> validationResults = new ConcurrentHashMap<>();
    @Value("${kafka.topic.validateUser}")
    private String validateUserTopic;

    CompletableFuture<Boolean> future = new CompletableFuture<>();

    @Transactional
    Order createOrder(OrderDTO orderDTO) throws ExecutionException, InterruptedException, TimeoutException, DataNotFoundException {
        validationResults.put(String.valueOf(orderDTO.getUserId()), future);
        kafkaTemplate.send(validateUserTopic, orderDTO.getUserId());

        Boolean isValid = future.get(5, TimeUnit.SECONDS);

        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        Order order = new Order();
        order.setNote(order.getNote());
        order.setUserId(orderDTO.getUserId());
        order.setOrderDate(LocalDate.now());
        order.setStatus("PENDING");
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            Float price = cartItemDTO.getPrice();
            orderDetail.setProductId(productId);
            orderDetail.setQuantity(quantity);
            orderDetail.setPrice(price);
            orderDetail.setTotalMoney(quantity * price);
            orderDetails.add(orderDetail);
        }
        return order;
    }


    public Order getOrder(Long id) throws DataNotFoundException {
        return orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("order not found"));
    }

    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        Order order = orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order not found"));
        validationResults.put(String.valueOf(orderDTO.getUserId()), future);
        kafkaTemplate.send(validateUserTopic, orderDTO.getUserId());

        Boolean isValid = future.get(5, TimeUnit.SECONDS);

        order.setNote(order.getNote());
        order.setUserId(orderDTO.getUserId());
        order.setOrderDate(LocalDate.now());
        order.setStatus(orderDTO.getStatus());
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if(order !=null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    public Page<Order> getOrders(String keyword, Long userId, Pageable pageable) throws DataNotFoundException {
        return orderRepository.searchOrders(userId, keyword, pageable);
    }

    @KafkaListener(topics = "${kafka.topic.validateUserResponse}", groupId = "order-group")
    public void listenValidationResponse(String message) {
        System.out.println(message);
        String[] parts = message.split(":");
        String userId = parts[0];
        boolean isValid = Boolean.parseBoolean(parts[1]);

        CompletableFuture<Boolean> future = validationResults.remove(userId);
        if (future != null) {
            future.complete(isValid);
        }
    }
}
