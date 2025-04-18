package com.orderservice.service;

import com.orderservice.dto.CartDTO;
import com.orderservice.dto.CartItemDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Cart;
import com.orderservice.model.CartItem;
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

    public Cart updateCart(CartDTO cartDTO) throws DataNotFoundException {
        Cart cart = cartRepository.findByUserId(cartDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("Cart not found"));
        for (CartItemDTO cartItemDTO : cartDTO.getCartItems()) {
            CartItem cartItem = new CartItem().builder()
                    .cart(cart)
                    .quantity(cartItemDTO.getQuantity())
                    .price(cartItemDTO.getPrice())
                    .productId(cartItemDTO.getProductId())
                    .build();
            cart.getCartItems().add(cartItem);
        }
        return cartRepository.save(cart);
    }

    public void deleteCartItems(Long userId) throws DataNotFoundException {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new DataNotFoundException("Cart not found"));
        cart.setCartItems(null);
    }


}
