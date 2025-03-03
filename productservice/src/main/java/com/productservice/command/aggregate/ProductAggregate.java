package com.productservice.command.aggregate;

import com.productservice.command.command.CreateProductCommand;
import com.productservice.command.event.ProductCreateEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@AllArgsConstructor
@NoArgsConstructor
public class ProductAggregate {
    @AggregateIdentifier
    private Long id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    private int stock;
    private Long categoryId;

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand){
        ProductCreateEvent productCreateEvent = new ProductCreateEvent();
        BeanUtils.copyProperties(createProductCommand, productCreateEvent);
        AggregateLifecycle.apply(productCreateEvent);
    }

    @EventSourcingHandler
    public void on(ProductCreateEvent productCreateEvent){
        this.id = productCreateEvent.getId();
        this.name = productCreateEvent.getName();
        this.price = productCreateEvent.getPrice();
        this.thumbnail = productCreateEvent.getThumbnail();
        this.description = productCreateEvent.getDescription();
        this.stock = productCreateEvent.getStock();
        this.categoryId = productCreateEvent.getCategoryId();
    }
}
