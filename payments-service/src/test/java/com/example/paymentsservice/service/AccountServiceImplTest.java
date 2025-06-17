package com.example.paymentsservice.service;

import com.example.paymentsservice.dto.CreateAccountRequest;
import com.example.paymentsservice.dto.DepositRequest;
import com.example.paymentsservice.exception.AccountNotFoundException;
import com.example.paymentsservice.model.Account;
import com.example.paymentsservice.model.OutboxEvent;
import com.example.paymentsservice.repository.AccountRepository;
import com.example.paymentsservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {

  @Mock private AccountRepository accountRepository;

  @Mock private OutboxEventRepository outboxEventRepository;

  @Mock private ObjectMapper objectMapper;

  @InjectMocks private AccountServiceImpl accountService;

  @Test
  public void createAccount_shouldCreateAccount_whenUserDoesNotExist() {
    CreateAccountRequest req = new CreateAccountRequest();
    req.setUserId(1337L);

    when(accountRepository.findByUserId(1337L)).thenReturn(Optional.empty());

    accountService.createAccount(req);

    verify(accountRepository, times(1)).save(any(Account.class));
  }

  @Test
  public void createAccount_shouldThrowException_whenUserExists() {
    CreateAccountRequest req = new CreateAccountRequest();
    req.setUserId(1337L);

    when(accountRepository.findByUserId(1337L)).thenReturn(Optional.of(new Account()));

    assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(req));
  }

  @Test
  public void getAccountBalance_shouldReturnBalance_whenUserExists() {
    Account acc = new Account(1337L, 1337L, new BigDecimal("1337.00"));
    when(accountRepository.findByUserId(1337L)).thenReturn(Optional.of(acc));

    var resp = accountService.getAccountBalance(1337L);

    assertEquals(new BigDecimal("1337.00"), resp.getBalance());
  }

  @Test
  public void getAccountBalance_shouldThrowException_whenUserDoesNotExist() {
    when(accountRepository.findByUserId(1337L)).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> accountService.getAccountBalance(1337L));
  }

  @Test
  void depositToAccount_shouldIncreaseBalance_whenUserExistsAndAmountIsPositive() throws JsonProcessingException {
    long uid = 1337L;
    BigDecimal bal = new BigDecimal("1337.00");
    BigDecimal dep = new BigDecimal("1337.00");
    Account acc = new Account(1337L, uid, bal);
    DepositRequest req = new DepositRequest();
    req.setAmount(dep);

    when(accountRepository.findByUserId(uid)).thenReturn(Optional.of(acc));
    when(objectMapper.writeValueAsString(any(DepositRequest.class))).thenReturn("{\"amount\":1337.00}");

    accountService.depositToAccount(uid, req);

    assertEquals(new BigDecimal("2674.00"), acc.getBalance());
    verify(accountRepository, times(1)).save(acc);
    verify(outboxEventRepository, times(1)).save(any(OutboxEvent.class));
  }

  @Test
  void depositToAccount_shouldThrowException_whenUserNotFound() {
    long uid = 1337L;
    DepositRequest req = new DepositRequest();
    req.setAmount(new BigDecimal("1337.00"));

    when(accountRepository.findByUserId(uid)).thenReturn(Optional.empty());

    assertThrows(AccountNotFoundException.class, () -> {
      accountService.depositToAccount(uid, req);
    });
  }
}
 