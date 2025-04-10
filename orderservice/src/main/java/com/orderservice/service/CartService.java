package com.orderservice.service;

import com.orderservice.dto.CartDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Cart;
import com.orderservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public Cart createCart(Long userId){
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    public Cart findCartByUserId(Long userId){
        return cartRepository.findByUserId(userId).get();
    }

    public Cart updateCart(Long userId, CartDTO cartDTO) throws DataNotFoundException {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new DataNotFoundException("Cart not found"));
        cart.setCartItems(cartDTO.getCartItems());
        return cartRepository.save(cart);
    }

    public void deleteCartItems(Long userId) throws DataNotFoundException {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new DataNotFoundException("Cart not found"));
        cart.setCartItems(null);
    }


}
