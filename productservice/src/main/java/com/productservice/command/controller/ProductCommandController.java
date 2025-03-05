package com.productservice.command.controller;

import com.productservice.command.command.CreateProductCommand;
import com.productservice.command.command.DeleteProductCommand;
import com.productservice.command.command.UpdateProductCommand;
import com.productservice.command.model.ProductRequestModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
public class ProductCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public String addProduct(@RequestBody ProductRequestModel model){
        CreateProductCommand command = new CreateProductCommand(UUID.randomUUID().toString(), model.getName(),model.getPrice()
                ,model.getThumbnail(),model.getDescription(),model.getStock(),model.getUserId(), model.getCategoryId());
        commandGateway.sendAndWait(command);
        return "Added product";
    }

    @PutMapping("/{id}")
    public String updateProduct(@RequestBody ProductRequestModel model){
        UpdateProductCommand command = new UpdateProductCommand(model.getId(), model.getName(),model.getPrice()
                ,model.getThumbnail(),model.getDescription(),model.getStock(),model.getUserId(), model.getCategoryId());
        commandGateway.sendAndWait(command);
        return "Product updated";
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable String id){
        DeleteProductCommand command = new DeleteProductCommand(id);
        commandGateway.sendAndWait(command);
        return "Product deleted";
    }
}
