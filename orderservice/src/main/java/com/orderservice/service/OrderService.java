package com.orderservice.service;

import com.orderservice.dto.CartItemDTO;
import com.orderservice.dto.OrderDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Order;
import com.orderservice.model.OrderDetail;
import com.orderservice.model.OrderStatus;
import com.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        Order order = new Order();
        order.setNote(order.getNote());
        order.setBuyerId(orderDTO.getBuyerId());
        order.setSellerId(order.getSellerId());
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
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


    public Order findOrderById(Long id) throws DataNotFoundException {
        return orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("order not found"));
    }

    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Order not found"));
        order.setNote(order.getNote());
        order.setBuyerId(orderDTO.getBuyerId());
        order.setSellerId(order.getSellerId());
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

    public Page<Order> searchOrders(String keyword, Long buyerId, Long sellerId, Pageable pageable) {
        return orderRepository.searchOrders(buyerId,sellerId, keyword, pageable);
    }

    public Page<Order> getAllOrders(Pageable pageable){
        return orderRepository.getAllOrders(pageable);
    }

}
