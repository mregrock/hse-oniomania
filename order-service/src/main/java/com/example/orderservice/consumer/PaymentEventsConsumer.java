package com.example.orderservice.consumer;

import com.example.orderservice.dto.PaymentProcessedEvent;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventsConsumer {

  private final OrderRepository orderRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "payments.processed")
  @Transactional
  public void consumePaymentProcessedEvent(String message) {
    log.info("Received payment processed event: {}", message);
    try {
      PaymentProcessedEvent event = objectMapper.readValue(message, PaymentProcessedEvent.class);
      Order order = orderRepository.findById(event.getOrderId())
            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + event.getOrderId()));

      if (order.getStatus() != OrderStatus.NEW) {
        log.warn("Order {} is not in NEW state, ignoring payment event", order.getId());
        return;
      }

      if (event.isSuccess()) {
        order.setStatus(OrderStatus.FINISHED);
      } else {
        order.setStatus(OrderStatus.CANCELLED);
      }
      orderRepository.save(order);
      log.info("Order {} status updated to {}", order.getId(), order.getStatus());

    } catch (JsonProcessingException e) {
        log.error("Failed to deserialize payment processed event", e);
    } catch (Exception e) {
        log.error("Error processing payment processed event", e);
    }
  }
}
 