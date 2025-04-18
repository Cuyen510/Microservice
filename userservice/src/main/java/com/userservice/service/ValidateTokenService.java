package com.userservice.service;

import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ValidateTokenService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserRepository userRepository;

    @Value("${kafka.topic.validateUserResponse}")
    private String validateUserResponse;

    @KafkaListener(topics = "${kafka.topic.validateUserRequest}", groupId = "user-service-group")
    public void validateToken(String username) {
        boolean exists = userRepository.existsByPhoneNumber(username);
        kafkaTemplate.send(validateUserResponse, username + ":" + exists);
    }




}
