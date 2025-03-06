package com.productservice.command.event;

import com.productservice.command.data.Product;
import com.productservice.command.data.ProductRepository;
import com.productservice.exceptions.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventsHandler {
    private final ProductRepository productRepository;
    @EventHandler
    public void on(ProductCreatedEvent event){
        Product product = new Product();
        BeanUtils.copyProperties(event,product);
        productRepository.save(product);
    }

    @EventHandler
    public void on(ProductUpdatedEvent event) throws DataNotFoundException {
        Product product = productRepository.findById(event.getId()).orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + event.getId()));
        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setThumbnail(event.getThumbnail());
        product.setDescription(event.getDescription());
        product.setStock(event.getStock());
        product.setUserId(event.getUserId());
        product.setCategoryId(event.getCategoryId());
        productRepository.save(product);
    }

    @EventHandler
    public void on(ProductDeletedEvent event) {
        productRepository.deleteById(event.getId());
    }
}
