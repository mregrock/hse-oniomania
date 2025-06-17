package com.example.paymentsservice.dto;

import lombok.Data;

/**
 * DTO for creating a new account.
 */
@Data
public class CreateAccountRequest {
  private Long userId;
}
