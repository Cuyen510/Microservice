package com.productservice.command.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestModel {
    private Long id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    private Long categoryId;
}
