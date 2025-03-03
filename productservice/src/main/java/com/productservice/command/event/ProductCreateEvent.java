package com.productservice.command.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateEvent {
    private Long id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    private Long categoryId;
}
