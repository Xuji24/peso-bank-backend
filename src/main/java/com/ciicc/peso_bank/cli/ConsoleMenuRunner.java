package com.ciicc.peso_bank.cli;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ciicc.peso_bank.dto.UserDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConsoleMenuRunner implements CommandLineRunner {

    private final AuthMenu authMenu;
    private final BankingMenu bankingMenu;
    private final ConsoleIO io;

    @Override
    public void run(String... args) {
        while (true) {
            io.banner("Peso Bank Application");
            io.println("1. Login");
            io.println("2. Sign Up");
            io.println("3. Exit Application");
            String choice = io.prompt("Choice");

            switch (choice) {
                case "1" -> enterBankingMenu(authMenu.login());
                case "2" -> enterBankingMenu(authMenu.signUp());
                case "3" -> {
                    return;
                }
                default -> io.println("Invalid choice.");
            }
        }
    }

    private void enterBankingMenu(Optional<UserDto> user) {
        user.ifPresent(bankingMenu::run);
    }
}
