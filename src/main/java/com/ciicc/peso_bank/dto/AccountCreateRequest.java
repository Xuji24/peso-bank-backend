package com.ciicc.peso_bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AccountCreateRequest(
    @NotNull Long userId,
    @NotBlank String accountType
) {}
