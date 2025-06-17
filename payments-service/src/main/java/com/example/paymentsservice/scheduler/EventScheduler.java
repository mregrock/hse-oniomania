package com.example.paymentsservice.scheduler;

import com.example.paymentsservice.model.OutboxEvent;
import com.example.paymentsservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventScheduler {

  private final OutboxEventRepository outboxEventRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Scheduled(fixedDelay = 10000)
  @Transactional
  public void processOutboxEvents() {
    List<OutboxEvent> events = outboxEventRepository.findTop100ByOrderByTimestampAsc();
    if (!events.isEmpty()) {
      log.info("Found {} payment events to process", events.size());
      for (OutboxEvent event : events) {
        try {
            log.info("Sending event id: {} to topic: {}", event.getId(), event.getTopic());
            kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload());
            outboxEventRepository.delete(event);
            log.info("Event id: {} sent and deleted from outbox", event.getId());
        } catch (Exception e) {
            log.error("Failed to send event {} to Kafka", event.getId(), e);
        }
      }
    }
  }
}
