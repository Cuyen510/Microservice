package com.orderservice.response;

import com.orderservice.model.Order;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String message;
    private Order order;
}
