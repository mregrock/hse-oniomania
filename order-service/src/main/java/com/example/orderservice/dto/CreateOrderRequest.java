package com.example.orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
  private Long userId;
  private String description;
  private BigDecimal amount;
}
