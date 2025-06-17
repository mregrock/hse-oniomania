package com.example.paymentsservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "processed_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

  @Id
  private UUID eventId;

}
