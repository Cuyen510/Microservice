package com.orderservice.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KafkaBridgeService {
    private final ConcurrentHashMap<String, CompletableFuture<String>> confirmationResults = new ConcurrentHashMap<>();

    public void put(String key, CompletableFuture<String> future) {
        confirmationResults.put(key, future);
    }

    public CompletableFuture<String> remove(String key) {
        return confirmationResults.remove(key);
    }
}

