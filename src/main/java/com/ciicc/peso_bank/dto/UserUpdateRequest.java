package com.ciicc.peso_bank.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
    String username,
    @NotBlank String password,
    @NotBlank String confirmPassword
) {}
