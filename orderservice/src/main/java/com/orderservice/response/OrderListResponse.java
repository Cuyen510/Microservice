package com.orderservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderListResponse {
    @JsonProperty("orders")
    private List<OrderResponse> orders;

    @JsonProperty("totalPages")
    private int totalPages;
}