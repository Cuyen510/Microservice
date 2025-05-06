package com.orderservice.response;

import com.orderservice.model.CartItem;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartResponse {
    private String message;
    private CartItem cartItem;
}
