package com.example.orderservice.dto;

import com.example.orderservice.model.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderResponse {
  private Long id;
  private Long userId;
  private String description;
  private BigDecimal amount;
  private OrderStatus status;
}
