package com.ciicc.peso_bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest (
    @NotBlank @Size(min = 4, max = 50) String username,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String confirmPassword
){}
