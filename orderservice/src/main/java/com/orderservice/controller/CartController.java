package com.orderservice.controller;

import com.orderservice.dto.CartDTO;
import com.orderservice.dto.CartItemDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.response.AddToCartResponse;
import com.orderservice.response.UpdateCartResponse;
import com.orderservice.service.CartService;
import com.orderservice.service.KafkaBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/{user_id}")
    public ResponseEntity<?> getCart(@PathVariable("user_id") Long userId) {

        return ResponseEntity.ok().body(cartService.findCartByUserId(userId));
    }

    @PostMapping("/{user_id}")
    public ResponseEntity<?> addToCart(@PathVariable("user_id") Long userId, @RequestBody CartItemDTO cartItemDTO, BindingResult result) throws DataNotFoundException {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(AddToCartResponse.builder().message(String.join(", ", errorMessages)).build());
        }
        return ResponseEntity.ok().body(AddToCartResponse.builder().cartItem(cartService.addToCart(userId, cartItemDTO)).message("Added to cart").build());
    }

    @PutMapping("/cartItem/{user_id}")
    public ResponseEntity<?> updateCartItem(@PathVariable("user_id") Long userId, @RequestBody CartItemDTO cartItemDTO) throws ExecutionException, InterruptedException, TimeoutException, DataNotFoundException {
        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaBridgeService.put(String.valueOf(userId), future);
        kafkaTemplate.send(productStockRequest, userId + "-" +"["+ cartItemDTO.toString()+"]");

        String response = future.get(10, TimeUnit.SECONDS);
        if (!response.equals("ok")) {
            return ResponseEntity.badRequest().body(UpdateCartResponse.builder().message("Not enough stock: " + response).build());
        }
        cartService.updateCartItem(userId, cartItemDTO);
        return ResponseEntity.ok().body(UpdateCartResponse.builder().message("Cart updated").build());
    }


    @PutMapping("")
    public ResponseEntity<?> updateCart(@RequestBody CartDTO cartDTO) throws DataNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaBridgeService.put(String.valueOf(cartDTO.getUserId()), future);
        kafkaTemplate.send(productStockRequest, cartDTO.getUserId()+"-"+cartDTO.getCartItems().toString());

        String response = future.get(5, TimeUnit.SECONDS);
        if (!response.equals("ok")) {
            return ResponseEntity.badRequest().body(UpdateCartResponse.builder().message("Not enough stock: " + response).build());
        }
        return ResponseEntity.ok().body(UpdateCartResponse.builder().cart(cartService.updateCart(cartDTO)).message("Cart updated").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCartItems(@PathVariable("id") Long userId) throws DataNotFoundException {
        cartService.deleteCartItems(userId);
        return ResponseEntity.ok().body("Cart items deleted");
    }

}
