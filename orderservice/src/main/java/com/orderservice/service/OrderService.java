package com.orderservice.service;

import com.orderservice.dto.CartItemDTO;
import com.orderservice.dto.OrderDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Order;
import com.orderservice.model.OrderDetail;
import com.orderservice.model.OrderStatus;
import com.orderservice.repository.OrderDetailRepository;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final KafkaBridgeService kafkaBridgeService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.productStockUpdate}")
    private String productStockUpdate;

    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        Order order = new Order();
        order.setFullname(orderDTO.getFullname());
        order.setNote(orderDTO.getNote());
        order.setUserId(orderDTO.getUserId());
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setShippingMethod(orderDTO.getShippingMethod());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setTrackingNumber(UUID.randomUUID().toString());
        order.setPhoneNumber(orderDTO.getPhoneNumber());
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order);

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
            orderDetailRepository.save(orderDetail);
        }
        return order;
    }


    public Order findOrderById(Long id) throws DataNotFoundException {
        return orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("order not found"));
    }

    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order not found"));
        order.setFullname(orderDTO.getFullname());
        order.setNote(orderDTO.getNote());
        order.setUserId(orderDTO.getUserId());
        order.setOrderDate(LocalDate.now());
        order.setStatus(orderDTO.getStatus());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setShippingMethod(orderDTO.getShippingMethod());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setTrackingNumber(orderDTO.getTrackingNumber());
        order.setPhoneNumber(orderDTO.getPhoneNumber());
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
    public void cancelOrder(Long id) throws DataNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        Order order = orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Can't find order with id: "+id));
        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaBridgeService.put(String.valueOf(id), future);
        kafkaTemplate.send(productStockUpdate, id+"-"+order.getOrderDetails().toString());

        String response = future.get(5, TimeUnit.SECONDS);

        if (response.equals("updated")) {
            order.setActive(false);
            orderRepository.save(order);
        }else{
            throw new RuntimeException("Invalid response");
        }
    }

    public Page<Order> searchOrders(String keyword, Long userId, Pageable pageable) {
        return orderRepository.searchOrders(userId, keyword, pageable);
    }

    public Page<Order> getAllOrders(Pageable pageable){
        return orderRepository.getAllOrders(pageable);
    }

}
