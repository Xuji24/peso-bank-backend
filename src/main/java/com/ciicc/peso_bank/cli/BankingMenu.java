package com.ciicc.peso_bank.cli;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.ciicc.peso_bank.dto.AccountCreateRequest;
import com.ciicc.peso_bank.dto.AccountDto;
import com.ciicc.peso_bank.dto.DepositRequest;
import com.ciicc.peso_bank.dto.TransactionDto;
import com.ciicc.peso_bank.dto.TransferRequest;
import com.ciicc.peso_bank.dto.UserDto;
import com.ciicc.peso_bank.dto.WithdrawRequest;
import com.ciicc.peso_bank.service.AccountService;
import com.ciicc.peso_bank.service.TransactionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BankingMenu {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ConsoleIO io;

    private UserDto currentUser;

    public void run(UserDto user) {
        currentUser = user;

        while (true) {
            io.banner("Peso Bank Application");
            io.println("1. Create Account");
            io.println("2. Balance Inquiry");
            io.println("3. List Accounts");
            io.println("4. Deposit");
            io.println("5. Withdraw");
            io.println("6. Transfer");
            io.println("7. Transaction History");
            io.println("0. Exit");
            String choice = io.prompt("Choice");

            try {
                switch (choice) {
                    case "1" -> createAccount();
                    case "2" -> balanceInquiry();
                    case "3" -> listAccounts();
                    case "4" -> deposit();
                    case "5" -> withdraw();
                    case "6" -> transfer();
                    case "7" -> transactionHistory();
                    case "0" -> {
                        return;
                    }
                    default -> io.println("Invalid choice.");
                }
            } catch (ResponseStatusException e) {
                io.println("Error: " + e.getReason());
            } catch (NumberFormatException e) {
                io.println("Error: please enter a valid number.");
            }
        }
    }

    private void createAccount() {
        Optional<String> accountType = io.promptOrBack("Account type");
        if (accountType.isEmpty()) {
            io.println("Cancelled.");
            return;
        }
        AccountDto account = accountService.createAccount(new AccountCreateRequest(currentUser.userId(), accountType.get()));
        io.println("Account created. Account number: " + account.accountNumber());
    }

    private void balanceInquiry() {
        AccountDto account = findOwnAccount();
        io.println("Account number: " + account.accountNumber());
        io.println("Balance: " + account.balance());
    }

    private void listAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        if (accounts.isEmpty()) {
            io.println("No accounts found.");
            return;
        }
        for (AccountDto account : accounts) {
            io.println(account.accountNumber() + " | " + account.accountType()
                + " | balance " + account.balance() + " | " + (account.active() ? "active" : "inactive"));
        }
    }

    private void deposit() {
        AccountDto account = findOwnAccount();
        Optional<String> amountInput = io.promptOrBack("Amount");
        if (amountInput.isEmpty()) {
            io.println("Cancelled.");
            return;
        }
        BigDecimal amount = new BigDecimal(amountInput.get());
        TransactionDto transaction = transactionService.deposit(new DepositRequest(account.accountId(), amount));
        io.println("Deposit successful. New reference: " + transaction.transactionId());
    }

    private void withdraw() {
        AccountDto account = findOwnAccount();
        Optional<String> amountInput = io.promptOrBack("Amount");
        if (amountInput.isEmpty()) {
            io.println("Cancelled.");
            return;
        }
        BigDecimal amount = new BigDecimal(amountInput.get());
        TransactionDto transaction = transactionService.withdraw(new WithdrawRequest(account.accountId(), amount));
        io.println("Withdraw successful. New reference: " + transaction.transactionId());
    }

    private void transfer() {
        AccountDto account = findOwnAccount();
        Optional<String> toAccountInput = io.promptOrBack("Destination account ID");
        if (toAccountInput.isEmpty()) {
            io.println("Cancelled.");
            return;
        }
        Optional<String> amountInput = io.promptOrBack("Amount");
        if (amountInput.isEmpty()) {
            io.println("Cancelled.");
            return;
        }
        Long toAccountId = Long.valueOf(toAccountInput.get());
        BigDecimal amount = new BigDecimal(amountInput.get());
        transactionService.transfer(new TransferRequest(account.accountId(), toAccountId, amount));
        io.println("Transfer successful.");
    }

    private void transactionHistory() {
        AccountDto account = findOwnAccount();
        List<TransactionDto> history = transactionService.getStatementForAccount(account.accountId());
        if (history.isEmpty()) {
            io.println("No transactions found.");
            return;
        }
        for (TransactionDto transaction : history) {
            io.println(transaction.transactionDate() + " | " + transaction.transactionType()
                + " | " + transaction.amount());
        }
    }

    private AccountDto findOwnAccount() {
        return accountService.getAllAccounts().stream()
            .filter(account -> account.userId().equals(currentUser.userId()))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No account found for this user. Create one first."));
    }
}
