package com.productservice.command.controller;

import com.productservice.command.command.CreateProductCommand;
import com.productservice.command.model.ProductRequestModel;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/products")
public class ProductCommandController {
    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String addProduct(@RequestBody ProductRequestModel model){
        CreateProductCommand command = new CreateProductCommand(model.getId(), model.getName(),model.getPrice()
                ,model.getThumbnail(),model.getDescription(),model.getStock(),model.getCategoryId());
        commandGateway.sendAndWait(command);
        return "Added product";
    }
}
