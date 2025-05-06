package com.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;

}
