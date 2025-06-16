package com.orderservice.service.CartService;

import com.orderservice.dto.CartDTO;
import com.orderservice.dto.CartItemDTO;
import com.orderservice.exceptions.DataNotFoundException;
import com.orderservice.model.Cart;
import com.orderservice.model.CartItem;
import com.orderservice.repository.CartItemRepository;
import com.orderservice.repository.CartRepository;
import com.orderservice.response.UpdateCartResponse;
import com.orderservice.service.KafkaBridgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Service
@RequiredArgsConstructor
public class CartService implements ICartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final KafkaBridgeService kafkaBridgeService;

    @Value("${kafka.topic.productStockRequest}")
    private String productStockRequest;
    public Cart createCart(Long userId){
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    public CartItem addToCart(Long userId, CartItemDTO cartItemDTO) throws Exception {
        Cart cart =  cartRepository.findByUserId(userId).orElseThrow(()-> new DataNotFoundException("Cant find cart"));
        List<Long> productIds = cart.getCartItems()
                .stream()
                .map(CartItem::getProductId)
                .toList();
        CartItem cartItem;
        CompletableFuture<String> future = new CompletableFuture<>();
        kafkaBridgeService.put(String.valueOf(userId), future);

        if(productIds.contains(cartItemDTO.getProductId())){
            cartItem = cartItemRepository.findByProductIdAndCartId(cartItemDTO.getProductId(), cart.getId()).orElseThrow(() -> new DataNotFoundException("Cant find cart"));
            int quantity = cartItemDTO.getQuantity()+cartItem.getQuantity();
            cartItemDTO.setQuantity(quantity);
            kafkaTemplate.send(productStockRequest, userId + "-" +"["+ cartItemDTO.toString()+"]");

            String response = future.get(10, TimeUnit.SECONDS);
            if (!response.equals("ok")) {
                throw new Exception("Not enough stock");
            }else cartItem.setQuantity(quantity);
        }else{
            kafkaTemplate.send(productStockRequest, userId + "-" +"["+ cartItemDTO.toString()+"]");
            String response = future.get(10, TimeUnit.SECONDS);
            if (!response.equals("ok")) {
                throw new Exception("Not enough stock");
            }else {
                cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setPrice(cartItemDTO.getPrice());
                cartItem.setProductId(cartItemDTO.getProductId());
                cartItem.setQuantity(cartItemDTO.getQuantity());
            }
        }
        return cartItemRepository.save(cartItem);
    }

    public CartItem updateCartItem(Long userId, CartItemDTO cartItemDTO) throws DataNotFoundException {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(()-> new DataNotFoundException("cant find user"));
        CartItem cartItem = cartItemRepository.findByProductIdAndCartId(cartItemDTO.getProductId(), cart.getId()).orElseThrow(()-> new DataNotFoundException("cant find product"));
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
