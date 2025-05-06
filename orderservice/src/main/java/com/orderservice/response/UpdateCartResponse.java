package com.orderservice.response;

import com.orderservice.model.Cart;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartResponse {
    private String message;
    private Cart cart;
}
