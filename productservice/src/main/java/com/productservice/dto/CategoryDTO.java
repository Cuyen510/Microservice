package com.productservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotNull(message = "Name can't be null")
    private String name;
}
