package com.ciicc.peso_bank.cli;

import java.util.Optional;
import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class ConsoleIO {

    private final Scanner scanner = new Scanner(System.in);

    public String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine().trim();
    }

    public Optional<String> promptOrBack(String label) {
        String value = prompt(label + " (or 'back')");
        if (value.equalsIgnoreCase("back")) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    public void banner(String title) {
        String line = "=".repeat(title.length() + 8);
        System.out.println();
        System.out.println(line);
        System.out.println("    " + title);
        System.out.println(line);
    }

    public void println(String message) {
        System.out.println(message);
    }
}
