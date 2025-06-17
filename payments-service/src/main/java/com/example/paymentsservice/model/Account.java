package com.example.paymentsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a user's payment account.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

  /**
   * The unique identifier for the account.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The ID of the user associated with this account.
   */
  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

  /**
   * The current balance of the account.
   */
  @Column(nullable = false)
  private BigDecimal balance;
} 