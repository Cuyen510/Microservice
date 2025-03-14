package com.userservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumerService {

    @KafkaListener(topics = "product-topic", groupId = "product-group",
            containerFactory = "productKafkaListenerFactory")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }
}
