package com.productservice.command.event;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductCreatedEvent {
    private String id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    private String userId;

    private Long categoryId;

}
