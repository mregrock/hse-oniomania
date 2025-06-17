package com.example.paymentsservice.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for depositing funds into an account.
 */
@Data
public class DepositRequest {
  private BigDecimal amount;
} 