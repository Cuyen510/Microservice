package com.productservice.query.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductResponseModel {
    private String id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    private String userId;

    private String categoryId;

}
