package com.productservice.service.ProductProducerService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductProducerService {
    private static final String TOPIC = "product-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProductProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC,message );
        System.out.println("Sent: " + message);
    }

    public void sendProduct(Product product) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        kafkaTemplate.send(TOPIC , objectMapper.writeValueAsString(product));
    }


}
