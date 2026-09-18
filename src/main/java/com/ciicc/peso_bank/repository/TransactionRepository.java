package com.ciicc.peso_bank.repository;

import com.ciicc.peso_bank.entity.Transaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    
    @Query("SELECT DISTINCT t FROM Transaction t LEFT JOIN FETCH t.account")
    List<Transaction> findAllWithTransaction();

    @Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.account WHERE t.transactionId = :id")
    Optional<Transaction> findWithTransactionById(Long id);

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :accountId ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);
}
