package com.ciicc.peso_bank.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ciicc.peso_bank.dto.AccountCreateRequest;
import com.ciicc.peso_bank.dto.AccountDto;
import com.ciicc.peso_bank.entity.Account;
import com.ciicc.peso_bank.entity.User;
import com.ciicc.peso_bank.mapper.AccountMapper;
import com.ciicc.peso_bank.repository.AccountRepository;
import com.ciicc.peso_bank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountDto createAccount(AccountCreateRequest request) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getAccount() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has an account");
        }

        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setDebt(BigDecimal.ZERO);
        account.setAccountType(request.accountType());
        account.setActive(true);
        account.setUser(user);

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findWithAccountById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAllWithAccounts().stream()
            .map(accountMapper::toDto)
            .toList();
    }

    private Long generateUniqueAccountNumber() {
        long candidate;
        do {
            candidate = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        } while (accountRepository.existsByAccountNumber(candidate));
        return candidate;
    }
}
