package com.orderservice.controller;

import com.orderservice.dto.CartDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/v1/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping("/id")
    public ResponseEntity<?> getCart(@PathVariable("user_id") Long userId){
        return ResponseEntity.ok().body(cartService.findCartByUserId(userId));
    }

    @PostMapping("/id")
    public ResponseEntity<?> createCart(@PathVariable("user_id") Long userId){
        return ResponseEntity.ok().body(cartService.createCart(userId));
    }

    @PutMapping("/id")
    public ResponseEntity<?> updateCart(@PathVariable("user_id") Long userId, CartDTO cartDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(cartService.updateCart(userId, cartDTO));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCartItems(@PathVariable("user_id") Long userId) throws DataNotFoundException {
        cartService.deleteCartItems(userId);
        return ResponseEntity.ok().body("Cart items deleted");
    }
}
