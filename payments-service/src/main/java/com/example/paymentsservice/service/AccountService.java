package com.example.paymentsservice.service;

import com.example.paymentsservice.dto.AccountResponse;
import com.example.paymentsservice.dto.CreateAccountRequest;
import com.example.paymentsservice.dto.DepositRequest;
import com.example.paymentsservice.dto.OrderCreatedEvent;

/**
 * Service interface for account management operations.
 */
public interface AccountService {

  /**
   * Creates a new account based on the provided request.
   * @param request The account creation request.
   */
  void createAccount(CreateAccountRequest request);

  /**
   * Retrieves the balance for a given user.
   * @param userId The ID of the user.
   * @return An object containing account details.
   */
  AccountResponse getAccountBalance(Long userId);

  /**
   * Deposits an amount into a user's account.
   * @param userId The ID of the user.
   * @param request The deposit request with the amount.
   */
  void depositToAccount(Long userId, DepositRequest request);

  /**
   * Processes a payment for an order.
   * @param event The order creation event.
   */
  void processPayment(OrderCreatedEvent event);
}
