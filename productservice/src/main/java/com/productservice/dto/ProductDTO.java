package com.productservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDTO {
    @NotBlank(message = "name can't be null")
    @Size(min = 3, max = 200,message = "name must be between 3 and 200 characters")
    private String name;

    @Min(value = 0, message = "price must be greater or equal to 0")
    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("category_id")
    private Long categoryId;
}
