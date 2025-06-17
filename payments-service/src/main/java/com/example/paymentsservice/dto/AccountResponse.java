package com.example.paymentsservice.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for returning account information.
 */
@Data
@Builder
public class AccountResponse {
  private Long userId;
  private BigDecimal balance;
} 