package com.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final KafkaBridgeService kafkaBridgeService;

    @KafkaListener(topics = "${kafka.topic.productStockResponse}", groupId = "order-group",containerFactory = "kafkaListenerFactory")
    public void listenConfirmationResponse(String message) {
        System.out.println("Received: " + message);
        String[] parts = message.split("-");
        String messageId = parts[0];
        CompletableFuture<String> future = kafkaBridgeService.remove(messageId);
        if (future != null) {
            future.complete(parts[1]);
        }
    }

    @KafkaListener(topics = "${kafka.topic.productStockUpdateResponse}", groupId = "order-group",containerFactory = "kafkaListenerFactory")
    public void listenConfirmationUpdateResponse(String message) {
        System.out.println("Received: " + message);
        String[] parts = message.split("-");
        String messageId = parts[0];
        CompletableFuture<String> future = kafkaBridgeService.remove(messageId);
        if (future != null) {
            future.complete(parts[1]);
        }
    }
}
