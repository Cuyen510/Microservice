package com.orderservice.service.OrderService;

import com.orderservice.dto.OrderDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws DataNotFoundException;

    Order findOrderById(Long id) throws DataNotFoundException;

    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;

    void cancelOrder(Long id) throws DataNotFoundException, ExecutionException, InterruptedException, TimeoutException;

    Page<Order> searchOrders(String keyword, Long userId, Pageable pageable);

    Page<Order> getAllOrdersByUserId(Long userId, Pageable pageable);
}
