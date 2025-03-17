package com.productservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductListResponse {
    @JsonProperty("products")
    private List<ProductResponse> products;

    @JsonProperty("totalPages")
    private int totalPages;
}
