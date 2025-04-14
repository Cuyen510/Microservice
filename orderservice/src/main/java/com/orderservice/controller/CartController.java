package com.orderservice.controller;

import com.orderservice.dto.CartDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/cart")
public class CartController {
    private final CartService cartService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<String>> confirmationResults = new ConcurrentHashMap<>();
    @Value("${kafka.topic.productStockRequest}")
    private String productStockRequest;

    @GetMapping("/id")
    public ResponseEntity<?> getCart(@PathVariable("user_id") Long userId){
        return ResponseEntity.ok().body(cartService.findCartByUserId(userId));
    }

    @PostMapping("/id")
    public ResponseEntity<?> createCart(@PathVariable("user_id") Long userId){
        return ResponseEntity.ok().body(cartService.createCart(userId));
    }

    @PutMapping("/id")
    public ResponseEntity<?> updateCart(@PathVariable("user_id") Long userId, CartDTO cartDTO) throws DataNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> future = new CompletableFuture<>();
        confirmationResults.put(String.valueOf(cartDTO.getUserId()), future);
        kafkaTemplate.send(productStockRequest, cartDTO.getUserId()+"-"+cartDTO.getCartItems().toString());

        String response = future.get(5, TimeUnit.SECONDS);
        return ResponseEntity.ok().body(cartService.updateCart(userId, cartDTO));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCartItems(@PathVariable("user_id") Long userId) throws DataNotFoundException {
        cartService.deleteCartItems(userId);
        return ResponseEntity.ok().body("Cart items deleted");
    }

    @KafkaListener(topics = "${kafka.topic.productStockResponse}", groupId = "order-group")
    public void listenConfirmationResponse(String message) {
        System.out.println(message);
        String[] parts = message.split("-");
        String userId = parts[0];
        CompletableFuture<String> future = confirmationResults.remove(userId);
        if (future != null) {
            future.complete(parts[1]);
        }
    }
}
