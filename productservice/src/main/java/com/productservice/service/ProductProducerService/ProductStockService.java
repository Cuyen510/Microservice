package com.productservice.service.ProductProducerService;


import com.productservice.exceptions.DataNotFoundException;
import com.productservice.model.Product;
import com.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStockService {
    @Value("${kafka.topic.productStockResponse}")
    private String productStockResponse;

    @Value("${kafka.topic.productStockUpdateResponse}")
    private String productStockUpdateResponse;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ProductRepository productRepository;

    @KafkaListener(topics = "${kafka.topic.productStockRequest}", groupId = "product-group")
    public void checkProductStock(String request) throws DataNotFoundException {
        String[] parts = request.split("-");
        String requestId = parts[0];
        int start = parts[1].indexOf('[') + 1;
        int end = parts[1].indexOf(']');
        parts[1] = parts[1].substring(start, end);
        List<String> itemList = new ArrayList<>();
        for (String item : List.of(parts[1].split(", "))) {
            String[] itemParts = item.split(":");
            Long productId = Long.valueOf(itemParts[0]);
            int quantity = Integer.valueOf(itemParts[1]);
            Product product = productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("product not found"));
            if (product.getStock() < quantity) {
                itemList.add(product.getName());
            }else{
                product.setStock(product.getStock()-quantity);
            }
        }
        String result;
        if(itemList.isEmpty()){
            result =  requestId+"-ok";
        }else {
            result = requestId + "-" + String.join(", ", itemList);
        }
        kafkaTemplate.send(productStockResponse, result);
    }
    @Transactional
    @KafkaListener(topics = "${kafka.topic.productStockUpdate}", groupId = "product-group")
    public void updateProductStock(String request) throws DataNotFoundException {
        String[] parts = request.split("-");
        String requestId = parts[0];
        int start = parts[1].indexOf('[') + 1;
        int end = parts[1].indexOf(']');
        parts[1] = parts[1].substring(start, end);
        for (String item : List.of(parts[1].split(", "))) {
            String[] itemParts = item.split(":");
            Long productId = Long.valueOf(itemParts[0]);
            int quantity = Integer.valueOf(itemParts[1]);
            Product product = productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("product not found"));
            product.setStock(product.getStock()+quantity);
            productRepository.save(product);
        }

        String result = requestId+"-"+"updated";

        kafkaTemplate.send(productStockUpdateResponse, result);
    }

}
