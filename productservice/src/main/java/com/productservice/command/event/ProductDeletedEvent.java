package com.productservice.command.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDeletedEvent {
    private String id;
}
