package com.productservice.command.aggregate;

import com.productservice.command.command.CreateProductCommand;
import com.productservice.command.command.DeleteProductCommand;
import com.productservice.command.command.UpdateProductCommand;
import com.productservice.command.event.ProductCreatedEvent;
import com.productservice.command.event.ProductDeletedEvent;
import com.productservice.command.event.ProductUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Aggregate
@AllArgsConstructor
@NoArgsConstructor
public class ProductAggregate {
    @AggregateIdentifier
    private String id;
    private String name;
    private Float price;
    private String thumbnail;
    private String description;
    private int stock;
    private String userId;
    private Long categoryId;

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand){
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
        AggregateLifecycle.apply(productCreatedEvent);
    }

    @CommandHandler
    public void handle(UpdateProductCommand updateProductCommand){
        ProductUpdatedEvent productUpdatedEvent = new ProductUpdatedEvent();
        BeanUtils.copyProperties(updateProductCommand, productUpdatedEvent);
        AggregateLifecycle.apply(productUpdatedEvent);
    }

    @CommandHandler
    public void handle(DeleteProductCommand deleteProductCommand){
        ProductDeletedEvent productDeletedEvent = new ProductDeletedEvent();
        BeanUtils.copyProperties(deleteProductCommand, productDeletedEvent);
        AggregateLifecycle.apply(productDeletedEvent);
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent){
        this.id = productCreatedEvent.getId();
        this.name = productCreatedEvent.getName();
        this.price = productCreatedEvent.getPrice();
        this.thumbnail = productCreatedEvent.getThumbnail();
        this.description = productCreatedEvent.getDescription();
        this.stock = productCreatedEvent.getStock();
        this.userId = productCreatedEvent.getUserId();
        this.categoryId = productCreatedEvent.getCategoryId();
    }

    @EventSourcingHandler
    public void on(ProductUpdatedEvent productUpdatedEvent){
        this.id = productUpdatedEvent.getId();
        this.name = productUpdatedEvent.getName();
        this.price = productUpdatedEvent.getPrice();
        this.thumbnail = productUpdatedEvent.getThumbnail();
        this.description = productUpdatedEvent.getDescription();
        this.stock = productUpdatedEvent.getStock();
        this.userId = productUpdatedEvent.getUserId();
        this.categoryId = productUpdatedEvent.getCategoryId();
    }

    @EventSourcingHandler
    public void on(ProductDeletedEvent productDeletedEvent){
        this.id = productDeletedEvent.getId();

    }
}
