package com.orderservice.service;

import com.orderservice.dto.CartDTO;
import com.orderservice.dto.CartItemDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Cart;
import com.orderservice.model.CartItem;
import com.orderservice.repository.CartItemRepository;
import com.orderservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    public Cart createCart(Long userId){
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    public CartItem addToCart(Long userId, CartItemDTO cartItemDTO) throws DataNotFoundException {
        Cart cart =  cartRepository.findByUserId(userId).orElseThrow(()-> new DataNotFoundException("Cant find cart"));
        List<Long> productIds = cart.getCartItems()
                .stream()
                .map(CartItem::getProductId)
                .toList();
        CartItem cartItem;
        if(productIds.contains(cartItemDTO.getProductId())){
            cartItem = cartItemRepository.findByProductIdAndCartId(cartItemDTO.getProductId(), cart.getId()).orElseThrow(() -> new DataNotFoundException("Cant find cart"));
            cartItem.setQuantity(cartItem.getQuantity()+cartItemDTO.getQuantity());
        }else{
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setPrice(cartItemDTO.getPrice());
            cartItem.setProductId(cartItemDTO.getProductId());
            cartItem.setQuantity(cartItemDTO.getQuantity());
        }
        return cartItemRepository.save(cartItem);
    }

    public CartItem updateCartItem(Long userId, CartItemDTO cartItemDTO) throws DataNotFoundException {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(()-> new DataNotFoundException("cant find user"));
        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(cartItemDTO.getProductId(), cart.getId()).orElseThrow(()-> new DataNotFoundException("cant find user"));
        if(cartItemDTO.getQuantity() == 0) cartItemRepository.delete(cartItem);
        else{
            cartItem.setQuantity(cartItemDTO.getQuantity());
        }
        return cartItemRepository.save(cartItem);
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
