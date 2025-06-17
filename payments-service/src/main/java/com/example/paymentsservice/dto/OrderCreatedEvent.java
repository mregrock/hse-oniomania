package com.example.paymentsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


/**
 * Event representing the creation of a new order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
  private UUID eventId;
  private Long orderId;
  private Long userId;
  private BigDecimal amount;
}
