package com.example.paymentsservice.service;

import com.example.paymentsservice.dto.AccountResponse;
import com.example.paymentsservice.dto.CreateAccountRequest;
import com.example.paymentsservice.dto.DepositRequest;
import com.example.paymentsservice.dto.OrderCreatedEvent;
import com.example.paymentsservice.dto.PaymentEvent;
import com.example.paymentsservice.exception.AccountNotFoundException;
import com.example.paymentsservice.model.Account;
import com.example.paymentsservice.model.OutboxEvent;
import com.example.paymentsservice.model.ProcessedEvent;
import com.example.paymentsservice.repository.AccountRepository;
import com.example.paymentsservice.repository.OutboxEventRepository;
import com.example.paymentsservice.repository.ProcessedEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service implementation for managing payment accounts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final OutboxEventRepository outboxEventRepository;
  private final ProcessedEventRepository processedEventRepository;
  private final ObjectMapper objectMapper;

  @Override
  @Transactional
  public void createAccount(CreateAccountRequest request) {
      if (accountRepository.findByUserId(request.getUserId()).isPresent()) {
          throw new IllegalArgumentException("Account for user " + request.getUserId() + " already exists.");
      }
      Account account = Account.builder()
              .userId(request.getUserId())
              .balance(BigDecimal.ZERO)
              .build();
      accountRepository.save(account);
  }

  @Override
  @Transactional(readOnly = true)
  public AccountResponse getAccountBalance(Long userId) {
    Account account = accountRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Account not found for user " + userId));
    return AccountResponse.builder()
            .userId(account.getUserId())
            .balance(account.getBalance())
            .build();
  }

  @Override
  @Transactional
  public void depositToAccount(Long userId, DepositRequest request) {
    log.info("Depositing {} to account of user {}", request.getAmount(), userId);
    Account account = accountRepository.findByUserId(userId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));

    account.setBalance(account.getBalance().add(request.getAmount()));
    accountRepository.save(account);
    try {
      String payload = objectMapper.writeValueAsString(request);
      OutboxEvent event = OutboxEvent.builder()
              .aggregateType("Account")
              .aggregateId(userId.toString())
              .eventType("DEPOSIT_SUCCESS")
              .payload(payload)
              .timestamp(LocalDateTime.now())
              .build();
      outboxEventRepository.save(event);
      log.info("Outbox event created for user {}", userId);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize payload for user {}", userId, e);
      throw new RuntimeException("Failed to create outbox event", e);
    }
  }

@Override
@Transactional
public void processPayment(OrderCreatedEvent event) {
  if (processedEventRepository.existsById(event.getEventId())) {
    log.warn("Event {} has already been processed.", event.getEventId());
    return;
  }

  Account account = accountRepository.findByUserId(event.getUserId())
          .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + event.getUserId()));

  String paymentStatus;
  if (account.getBalance().compareTo(event.getAmount()) >= 0) {
    account.setBalance(account.getBalance().subtract(event.getAmount()));
    accountRepository.save(account);
    paymentStatus = "PAYMENT_SUCCESS";
    log.info("Payment successful for order {}", event.getOrderId());
  } else {
    paymentStatus = "PAYMENT_FAILURE";
    log.warn("Insufficient funds for order {}. Current balance: {}, required: {}",
            event.getOrderId(), account.getBalance(), event.getAmount());
  }

  try {
    PaymentEvent paymentEvent = PaymentEvent.builder()
            .orderId(event.getOrderId())
            .userId(event.getUserId())
            .amount(event.getAmount())
            .status(paymentStatus)
            .build();
    String payload = objectMapper.writeValueAsString(paymentEvent);

    OutboxEvent outboxEvent = OutboxEvent.builder()
            .aggregateType("Payment")
            .aggregateId(event.getOrderId().toString())
            .eventType(paymentStatus)
            .payload(payload)
            .timestamp(LocalDateTime.now())
            .build();
    outboxEventRepository.save(outboxEvent);
    processedEventRepository.save(new ProcessedEvent(event.getEventId()));

    log.info("Outbox event {} created for order {}", paymentStatus, event.getOrderId());
  } catch (JsonProcessingException e) {
    log.error("Failed to serialize payment event payload for order {}", event.getOrderId(), e);
    throw new RuntimeException("Failed to create outbox event", e);
  }
}
}
