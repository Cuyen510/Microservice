package com.orderservice.controller;

import com.orderservice.dto.OrderDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.service.KafkaBridgeService;
import com.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final KafkaBridgeService kafkaBridgeService;
    @Value("${kafka.topic.productStockRequest}")
    private String productStockRequest;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDTO orderDTO, BindingResult result) throws Exception{
        if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError:: getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        };

        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaBridgeService.put(orderDTO.getPhoneNumber(), future);
        kafkaTemplate.send(productStockRequest, orderDTO.getPhoneNumber()+"-"+orderDTO.getCartItems().toString());

        String response = future.get(5, TimeUnit.SECONDS);

        if (!response.equals("ok")) {
            return ResponseEntity.badRequest().body("Not enough stock: " + response);
        }

        return ResponseEntity.ok().body(orderService.createOrder(orderDTO));
    }

    @GetMapping("")
    public ResponseEntity<?> getAllOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit){
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("orderDate").descending()
        );
        return ResponseEntity.ok().body(orderService.getAllOrders(pageRequest));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") Long buyerId,
            @RequestParam(defaultValue = "") Long sellerId) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("orderDate").descending()
        );
        return ResponseEntity.ok().body(orderService.searchOrders(keyword,buyerId,sellerId,pageRequest));
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<?> searchOrderById(@PathVariable("id") Long id) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderService.findOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Long id, @RequestBody OrderDTO orderDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable("id") Long id) throws DataNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().body("order canceled");
    }

}
