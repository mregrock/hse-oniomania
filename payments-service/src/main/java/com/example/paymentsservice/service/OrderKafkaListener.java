package com.example.paymentsservice.service;

import com.example.paymentsservice.dto.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderKafkaListener {

  private final AccountService accountService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "orders.created", groupId = "payments-group")
  public void handleOrderCreated(String message) {
    try {
        OrderCreatedEvent event = objectMapper.readValue(message, OrderCreatedEvent.class);
        log.info("Received order created event: {}", event);
        accountService.processPayment(event);
    } catch (IOException e) {
        log.error("Failed to deserialize message: {}", message, e);
    }
  }
}
