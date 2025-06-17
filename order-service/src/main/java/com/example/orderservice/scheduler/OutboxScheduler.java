package com.example.orderservice.scheduler;

import com.example.orderservice.model.Outbox;
import com.example.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

  private final OutboxRepository outboxRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Scheduled(fixedDelay = 10000)
  @Transactional
  public void processOutboxEvents() {
    List<Outbox> events = outboxRepository.findTop100ByOrderByCreatedAtAsc();
    if (!events.isEmpty()) {
      log.info("Found {} events to process", events.size());
      for (Outbox event : events) {
        log.info("Processing outbox event: {}", event.getId());
        try {
          kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload()).get();
          log.info("Message for aggregate id {} sent successfully to topic {}", event.getAggregateId(), event.getTopic());
          outboxRepository.delete(event);
        } catch (Exception e) {
          log.error("Failed to send message for aggregate id {} to topic {}. It will be retried.", event.getAggregateId(), event.getTopic(), e);
        }
      }
    }
  }
}
