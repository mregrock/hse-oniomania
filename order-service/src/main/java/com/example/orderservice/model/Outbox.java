package com.example.orderservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Data
@NoArgsConstructor
public class Outbox {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String aggregateType;

  @Column(nullable = false)
  private String aggregateId;

  @Column(nullable = false)
  private String topic;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String payload;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public Outbox(String aggregateType, String aggregateId, String topic, String payload) {
    this.aggregateType = aggregateType;
    this.aggregateId = aggregateId;
    this.topic = topic;
    this.payload = payload;
  }
}
