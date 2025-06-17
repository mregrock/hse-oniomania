package com.example.paymentsservice.controller;

import com.example.paymentsservice.dto.AccountResponse;
import com.example.paymentsservice.dto.CreateAccountRequest;
import com.example.paymentsservice.dto.DepositRequest;
import com.example.paymentsservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing payment accounts.
 */
@RestController
@RequestMapping("/api/v1/payments/accounts")
@RequiredArgsConstructor
public class AccountController {

  private final AccountService accountService;

  /**
   * Creates a new payment account for a user.
   * @param request The request containing the user ID.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createAccount(@RequestBody CreateAccountRequest request) {
    accountService.createAccount(request);
  }

  /**
   * Retrieves the balance of a user's payment account.
   * @param userId The ID of the user.
   * @return The account details with the current balance.
   */
  @GetMapping("/{userId}")
  public AccountResponse getAccountBalance(@PathVariable Long userId) {
    return accountService.getAccountBalance(userId);
  }

  /**
   * Deposits a specified amount into a user's payment account.
   * @param userId The ID of the user.
   * @param request The request containing the amount to deposit.
   */
  @PostMapping("/{userId}/deposit")
  @ResponseStatus(HttpStatus.OK)
  public void deposit(@PathVariable Long userId, @RequestBody DepositRequest request) {
    accountService.depositToAccount(userId, request);
  }
}
