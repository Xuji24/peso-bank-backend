package com.ciicc.peso_bank.repository;

import com.ciicc.peso_bank.entity.Account;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN a.user")
    List<Account> findAllWithAccounts();

    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN a.user WHERE a.accountId = :id")
    Optional<Account> findWithAccountById(Long id);

    boolean existsByAccountNumber(Long accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountId = :id")
    Optional<Account> findByIdForUpdate(Long id);
}