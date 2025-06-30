package com.userservice.service;

import com.userservice.exceptions.DataNotFoundException;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@EnableAsync
public class ValidateTokenService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserRepository userRepository;

    @Value("${kafka.topic.validateUserResponse}")
    private String validateUserResponse;

    @Async
    @KafkaListener(topics = "${kafka.topic.validateUserRequest}", groupId = "user-service-group")
    public void validateToken(String request) throws DataNotFoundException {
        String[] parts = request.split("-");
        String phoneNumber = parts[0];
        String role = parts[1];
        boolean phoneValidate = userRepository.existsByPhoneNumber(phoneNumber);
        boolean roleValidate = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(()->new DataNotFoundException("Cant find user"))
                        .getRole().getName().equals(role);
        boolean validation = phoneValidate && roleValidate;
        kafkaTemplate.send(validateUserResponse, phoneNumber + ":" + validation);
    }


}
