package com.productservice.command.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRequestModel {
    private String id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    private String userId;

    private String categoryId;

}
