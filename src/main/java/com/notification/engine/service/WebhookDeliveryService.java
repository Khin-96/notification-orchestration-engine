package com.notification.engine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookDeliveryService {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${notification.webhook.timeout-seconds}")
    private int timeoutSeconds;
    
    @Value("${notification.webhook.max-retries}")
    private int maxRetries;
    
    @Value("${notification.retry.backoff-delays}")
    private String backoffDelays;
    
    @Async
    public void deliverWebhook(String webhookUrl, String notificationId, String status) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            return;
        }
        
        Map<String, String> payload = Map.of(
                "notificationId", notificationId,
                "status", status,
                "timestamp", String.valueOf(System.currentTimeMillis())
        );
        
        WebClient webClient = webClientBuilder.build();
        
        webClient.post()
                .uri(webhookUrl)
                .bodyValue(payload)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    log.error("Webhook delivery failed with status: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Webhook failed"));
                })
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .retryWhen(Retry.backoff(maxRetries, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofHours(2)))
                .doOnSuccess(response -> log.info("Webhook delivered successfully to: {}", webhookUrl))
                .doOnError(error -> log.error("Webhook delivery failed after retries to: {}", webhookUrl, error))
                .subscribe();
    }
}
