package com.productservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeleteProductCommand {
    @TargetAggregateIdentifier
    private String id;
}
