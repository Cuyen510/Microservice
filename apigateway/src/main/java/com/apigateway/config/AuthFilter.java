package com.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<Boolean>> validationResults = new ConcurrentHashMap<>();

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${kafka.topic.validateUser}")
    private String validateUserTopic;

    public static class Config {}

    public AuthFilter(KafkaTemplate<String, String> kafkaTemplate) {
        super(Config.class);
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization information"));
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization structure"));
            }

            String token = authHeader.substring(7);
            DecodedJWT decodedJWT;
            try {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
                decodedJWT = JWT.require(algorithm).build().verify(token);
            } catch (JWTVerificationException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token verification failed"));
            }

            String username = decodedJWT.getSubject();
            String role = decodedJWT.getClaim("role").asString();
            String userId = decodedJWT.getClaim("userId").asString();

            if (username == null || username.isEmpty() || role == null) {
                return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
            }

            CompletableFuture<Boolean> future = new CompletableFuture<>();
            validationResults.put(username, future);
            kafkaTemplate.send(validateUserTopic, username);

            return Mono.fromFuture(() -> future)
                    .timeout(Duration.ofSeconds(20))
                    .flatMap(isValid -> {
                        if (!isValid) {
                            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
                        }

                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-auth-user", username)
                                .header("X-auth-userId", userId)
                                .header("X-auth-role", role)
                                .build();

                        return chain.filter(exchange.mutate().request(request).build());
                    })
                    .onErrorMap(ex -> {
                        if (ex instanceof TimeoutException) {
                            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User validation timeout");
                        }
                        return ex;
                    });
        };
    }



    @KafkaListener(topics = "${kafka.topic.validateUserResponse}", groupId = "gateway-group")
    public void listenValidationResponse(String message) {
        System.out.println(message);
        String[] parts = message.split(":");
        String username = parts[0];
        boolean isValid = Boolean.parseBoolean(parts[1]);

        CompletableFuture<Boolean> future = validationResults.remove(username);
        if (future != null) {
            future.complete(isValid);
        }
    }
}
