package com.productservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCommand {
    @TargetAggregateIdentifier
    private Long id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    private Long categoryId;
}
