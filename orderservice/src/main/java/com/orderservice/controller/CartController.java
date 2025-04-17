package com.orderservice.controller;

import com.orderservice.dto.CartDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.service.CartService;
import com.orderservice.service.KafkaBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    private final KafkaBridgeService kafkaBridgeService;
    @Value("${kafka.topic.productStockRequest}")
    private String productStockRequest;

    @GetMapping("/{id}")
    public ResponseEntity<?> getCart(@PathVariable("id") Long userId){
        return ResponseEntity.ok().body(cartService.findCartByUserId(userId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createCart(@PathVariable("id") Long userId){
        return ResponseEntity.ok().body(cartService.createCart(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart(CartDTO cartDTO) throws DataNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaBridgeService.put(String.valueOf(cartDTO.getUserId()), future);
        kafkaTemplate.send(productStockRequest, cartDTO.getUserId()+"-"+cartDTO.getCartItems().toString());

        String response = future.get(5, TimeUnit.SECONDS);
        if (response != null && !response.isBlank()) {
            return ResponseEntity.badRequest().body("Not enough stock: " + response);
        }
        return ResponseEntity.ok().body(cartService.updateCart(cartDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartItems(@PathVariable("id") Long userId) throws DataNotFoundException {
        cartService.deleteCartItems(userId);
        return ResponseEntity.ok().body("Cart items deleted");
    }

}
