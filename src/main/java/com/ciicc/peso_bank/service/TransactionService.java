package com.ciicc.peso_bank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ciicc.peso_bank.dto.DepositRequest;
import com.ciicc.peso_bank.dto.TransactionDto;
import com.ciicc.peso_bank.dto.TransferRequest;
import com.ciicc.peso_bank.dto.WithdrawRequest;
import com.ciicc.peso_bank.entity.Account;
import com.ciicc.peso_bank.entity.Transaction;
import com.ciicc.peso_bank.mapper.TransactionMapper;
import com.ciicc.peso_bank.repository.AccountRepository;
import com.ciicc.peso_bank.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final String DEPOSIT = "DEPOSIT";
    private static final String WITHDRAWAL = "WITHDRAWAL";
    private static final String TRANSFER_OUT = "TRANSFER_OUT";
    private static final String TRANSFER_IN = "TRANSFER_IN";

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AuditService auditService;

    @Transactional
    public TransactionDto deposit(DepositRequest request) {
        Account account = lockActiveAccount(request.accountId());

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        Transaction transaction = recordTransaction(account, null, request.amount(), DEPOSIT);

        auditService.log(account.getUser(),
            "Deposited %.2f to account %d".formatted(request.amount(), account.getAccountNumber()));

        return transactionMapper.toDto(transaction);
    }

    @Transactional
    public TransactionDto withdraw(WithdrawRequest request) {
        Account account = lockActiveAccount(request.accountId());
        requireSufficientBalance(account, request.amount());

        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);

        Transaction transaction = recordTransaction(account, null, request.amount(), WITHDRAWAL);

        auditService.log(account.getUser(),
            "Withdrew %.2f from account %d".formatted(request.amount(), account.getAccountNumber()));

        return transactionMapper.toDto(transaction);
    }

    @Transactional
    public TransactionDto transfer(TransferRequest request) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot transfer to the same account");
        }

        // Lock accounts in a consistent order to avoid deadlocks between concurrent transfers.
        Long firstId = Math.min(request.fromAccountId(), request.toAccountId());
        Long secondId = Math.max(request.fromAccountId(), request.toAccountId());
        Account first = lockActiveAccount(firstId);
        Account second = lockActiveAccount(secondId);

        Account fromAccount = request.fromAccountId().equals(firstId) ? first : second;
        Account toAccount = request.fromAccountId().equals(firstId) ? second : first;

        requireSufficientBalance(fromAccount, request.amount());

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));
        toAccount.setBalance(toAccount.getBalance().add(request.amount()));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        recordTransaction(toAccount, fromAccount.getAccountId(), request.amount(), TRANSFER_IN);
        Transaction outgoing = recordTransaction(fromAccount, toAccount.getAccountId(), request.amount(), TRANSFER_OUT);

        auditService.log(fromAccount.getUser(),
            "Transferred %.2f to account %d".formatted(request.amount(), toAccount.getAccountNumber()));
        auditService.log(toAccount.getUser(),
            "Received %.2f from account %d".formatted(request.amount(), fromAccount.getAccountNumber()));

        return transactionMapper.toDto(outgoing);
    }

    @Transactional(readOnly = true)
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findWithTransactionById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        return transactionMapper.toDto(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getStatementForAccount(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId).stream()
            .map(transactionMapper::toDto)
            .toList();
    }

    private Account lockActiveAccount(Long accountId) {
        Account account = accountRepository.findByIdForUpdate(accountId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (!account.isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account is not active");
        }
        return account;
    }

    private void requireSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient balance");
        }
    }

    private Transaction recordTransaction(Account account, Long receiverAccountId, BigDecimal amount, String type) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setReceiverAccountId(receiverAccountId);
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setTransactionDate(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }
}
