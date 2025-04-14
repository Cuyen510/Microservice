package com.productservice.service.ProductProducerService;


import com.productservice.exceptions.DataNotFoundException;
import com.productservice.model.Product;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductStockService {
    @Value("${kafka.topic.productStockResponse}")
    private String productStockResponse;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ProductRepository productRepository;

    @KafkaListener(topics = "${kafka.topic.productStockRequest}", groupId = "product-group")
    public void checkProductStock(String request) throws DataNotFoundException {
        String[] parts = request.split("-");
        String requestId = parts[0];
        List<String> itemlist = List.of(parts[1].split(":"));
        for (String item : itemlist) {
            String[] itemParts = item.split(":");
            Long productId = Long.valueOf(itemParts[0]);
            int quantity = Integer.valueOf(itemParts[1]);
            Product product = productRepository.findById(productId).orElseThrow(() -> new DataNotFoundException("product not found"));
            if (product.getStock() < quantity) {
                itemlist.add(productId + ":" + "out");
            }
        }

        String result = requestId+"-"+String.join(", ", itemlist);

        kafkaTemplate.send("${kafka.topic.productStockResponse}", result);
    }

}
