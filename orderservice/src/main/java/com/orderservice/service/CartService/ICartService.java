package com.orderservice.service.CartService;

import com.orderservice.dto.CartDTO;
import com.orderservice.dto.CartItemDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Cart;
import com.orderservice.model.CartItem;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ICartService {
    Cart createCart(Long userId);

    CartItem addToCart(Long userId, CartItemDTO cartItemDTO) throws Exception;

    CartItem updateCartItem(Long userId, CartItemDTO cartItemDTO) throws DataNotFoundException;

    Cart findCartByUserId(Long userId);

    Cart updateCart(CartDTO cartDTO) throws DataNotFoundException;

    void deleteCartItems(Long userId) throws DataNotFoundException;
}

