package com.productservice.command.event;

import com.productservice.command.data.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    @EventHandler
    public void on(ProductCreatedEvent event) throws DataNotFoundException {
        Product product = new Product();
        product.setName(event.getName());
        product.setPrice(event.getPrice());
        product.setThumbnail(event.getThumbnail());
        product.setDescription(event.getDescription());
        product.setStock(event.getStock());
        product.setUserId(event.getUserId());
        product.setCategory(categoryRepository.findById(event.getCategoryId()).orElseThrow(() -> new DataNotFoundException("Cant find category")));
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
        product.setCategory(categoryRepository.findById(event.getCategoryId()).orElseThrow(() -> new DataNotFoundException("Cant find category")));
        productRepository.save(product);
    }

    @EventHandler
    public void on(ProductDeletedEvent event) {
        productRepository.deleteById(event.getId());
    }
}
