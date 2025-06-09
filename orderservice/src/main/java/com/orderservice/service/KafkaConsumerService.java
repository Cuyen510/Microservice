package com.orderservice.service;

import com.orderservice.service.CartService.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final KafkaBridgeService kafkaBridgeService;

    private final CartService cartService;

    @KafkaListener(topics = "${kafka.topic.productStockResponse}", groupId = "order-group")
    public void listenConfirmationResponse(String message) {
        System.out.println("Received: " + message);
        String[] parts = message.split("-");
        String messageId = parts[0];
        CompletableFuture<String> future = kafkaBridgeService.remove(messageId);
        if (future != null) {
            future.complete(parts[1]);
        }
    }

    @KafkaListener(topics = "${kafka.topic.productStockUpdateResponse}", groupId = "order-group")
    public void listenConfirmationUpdateResponse(String message) {
        System.out.println("Received: " + message);
        String[] parts = message.split("-");
        String messageId = parts[0];
        CompletableFuture<String> future = kafkaBridgeService.remove(messageId);
        if (future != null) {
            future.complete(parts[1]);
        }
    }

    @KafkaListener(topics = "${kafka.topic.createCart}", groupId = "order-group")
    public void listenCreateCartMessage(String message) {
        System.out.println("Received: " + message);
        Long userId = Long.valueOf(message);
        cartService.createCart(userId);
    }
}
