package com.productservice.command.event;

import com.productservice.command.data.Product;
import com.productservice.command.data.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventsHandler {
    private final ProductRepository productRepository;

    public void on(ProductCreateEvent event){
        Product product = new Product();
        BeanUtils.copyProperties(event,product);
        productRepository.save(product);
    }
}
