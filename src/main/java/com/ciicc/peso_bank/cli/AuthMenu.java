package com.ciicc.peso_bank.cli;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.ciicc.peso_bank.dto.AccountCreateRequest;
import com.ciicc.peso_bank.dto.AccountDto;
import com.ciicc.peso_bank.dto.DepositRequest;
import com.ciicc.peso_bank.dto.UserCreateRequest;
import com.ciicc.peso_bank.dto.UserDto;
import com.ciicc.peso_bank.service.AccountService;
import com.ciicc.peso_bank.service.TransactionService;
import com.ciicc.peso_bank.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthMenu {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ConsoleIO io;

    public Optional<UserDto> login() {
        io.banner("Login");
        while (true) {
            Optional<String> username = io.promptOrBack("Username");
            if (username.isEmpty()) {
                return Optional.empty();
            }
            Optional<String> password = io.promptOrBack("Password");
            if (password.isEmpty()) {
                return Optional.empty();
            }

            if (username.get().isBlank() || password.get().isBlank()) {
                io.println("Username and password are required.");
                continue;
            }

            try {
                UserDto user = userService.login(username.get(), password.get());
                io.println("Login successful. Welcome, " + user.username() + ".");
                return Optional.of(user);
            } catch (ResponseStatusException e) {
                io.println("Error: " + e.getReason());
            }
        }
    }

    public Optional<UserDto> signUp() {
        io.banner("Sign Up");

        UserDto newUser;
        while (true) {
            Optional<String> username = io.promptOrBack("Username");
            if (username.isEmpty()) {
                return Optional.empty();
            }
            Optional<String> password = io.promptOrBack("Password");
            if (password.isEmpty()) {
                return Optional.empty();
            }
            Optional<String> confirmPassword = io.promptOrBack("Confirm password");
            if (confirmPassword.isEmpty()) {
                return Optional.empty();
            }

            if (username.get().isBlank() || password.get().isBlank() || confirmPassword.get().isBlank()) {
                io.println("All fields are required.");
                continue;
            }

            try {
                newUser = userService.createUser(
                    new UserCreateRequest(username.get(), password.get(), confirmPassword.get()));
                break;
            } catch (ResponseStatusException e) {
                io.println("Error: " + e.getReason());
            }
        }

        Optional<String> accountType = io.promptOrBack("Account type");
        if (accountType.isEmpty()) {
            io.println("Skipped account creation. You can create one from the menu.");
            return Optional.of(newUser);
        }

        AccountDto account;
        try {
            account = accountService.createAccount(new AccountCreateRequest(newUser.userId(), accountType.get()));
        } catch (ResponseStatusException e) {
            io.println("Error: " + e.getReason());
            return Optional.of(newUser);
        }

        BigDecimal initialDeposit;
        while (true) {
            Optional<String> depositInput = io.promptOrBack("Initial deposit");
            if (depositInput.isEmpty()) {
                io.println("Skipped initial deposit.");
                return Optional.of(newUser);
            }
            try {
                initialDeposit = new BigDecimal(depositInput.get());
            } catch (NumberFormatException e) {
                io.println("Error: please enter a valid number.");
                continue;
            }
            if (initialDeposit.compareTo(BigDecimal.valueOf(0.01)) < 0) {
                io.println("Error: initial deposit must be at least 0.01.");
                continue;
            }
            break;
        }

        transactionService.deposit(new DepositRequest(account.accountId(), initialDeposit));

        io.println("Account created. Account number: " + account.accountNumber()
            + " | Balance: " + initialDeposit);
        return Optional.of(newUser);
    }
}
