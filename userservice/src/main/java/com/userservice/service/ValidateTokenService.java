package com.userservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ValidateTokenService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserRepository userRepository;

    @KafkaListener(topics = "${kafka.topic.validateUser}", groupId = "user-service-group")
    public void validateToken(String username) {
        boolean exists = userRepository.existsByPhoneNumber(username);
        kafkaTemplate.send("${kafka.topic.validateUserResponse}", username + ":" + exists);
    }




}
