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

  @Scheduled(fixedRate = 10000)
  @Transactional
  public void processOutbox() {
    List<Outbox> events = outboxRepository.findAll();
    if (events.isEmpty()) {
      return;
    }
    log.info("Found {} events to process", events.size());
    for (Outbox event : events) {
      log.info("Processing outbox event: {}", event.getId());
      try {
        kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
              .whenComplete((result, ex) -> {
                if (ex == null) {
                  log.info("Message for aggregate id {} sent successfully to topic {}", event.getAggregateId(), event.getTopic());
                  outboxRepository.delete(event);
                } else {
                  log.error("Failed to send message for aggregate id {}", event.getAggregateId(), ex);
                }
              });
      } catch (Exception e) {
        log.error("Error sending event to kafka, id: {}", event.getId(), e);
      }
    }
  }
}
