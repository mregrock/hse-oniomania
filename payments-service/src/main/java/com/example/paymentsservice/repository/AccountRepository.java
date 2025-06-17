package com.example.paymentsservice.repository;

import com.example.paymentsservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Account entity.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  /**
   * Finds an account by the user's ID.
   * @param userId The ID of the user.
   * @return An Optional containing the account if found.
   */
  Optional<Account> findByUserId(Long userId);
}
