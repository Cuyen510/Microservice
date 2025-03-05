package com.productservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCommand {
    @TargetAggregateIdentifier
    private String id;

    private String name;

    private Float price;

    private String thumbnail;

    private String description;

    private int stock;

    private String userId;

    private String categoryId;

}
