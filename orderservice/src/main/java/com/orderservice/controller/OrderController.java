package com.orderservice.controller;

import com.orderservice.dto.OrderDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<String>> confirmationResults = new ConcurrentHashMap<>();
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
        confirmationResults.put(orderDTO.getTrackingNumber(), future);
        kafkaTemplate.send(productStockRequest, orderDTO.getTrackingNumber()+"-"+orderDTO.getCartItems().toString());

        String response = future.get(5, TimeUnit.SECONDS);

        if (response != null) {
            String[] parts = response.split(", ");
            StringBuilder sb = new StringBuilder();
            for (String part : parts) {
                String[] itemParts = part.split(":");
                sb.append(itemParts[0]).append(", ");
            }
            throw new Exception(sb+ "out of stock");
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

    @GetMapping("/search/id")
    public ResponseEntity<?> searchOrderById(@PathVariable("id") Long id) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderService.findOrderById(id));
    }

    @PutMapping("/id")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Long id, @RequestBody OrderDTO orderDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/id")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Long id){
        orderService.deleteOrder(id);
        return ResponseEntity.ok().body("order deleted");
    }


    @KafkaListener(topics = "${kafka.topic.productStockResponse}", groupId = "order-group")
    public void listenValidationResponse(String message) {
        System.out.println(message);
        String[] parts = message.split("-");
        String trackingNumber = parts[0];
        CompletableFuture<String> future = confirmationResults.remove(trackingNumber);
        if (future != null) {
            future.complete(parts[1]);
        }
    }
}
