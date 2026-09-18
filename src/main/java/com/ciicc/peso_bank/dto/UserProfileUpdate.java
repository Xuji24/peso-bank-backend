package com.ciicc.peso_bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record UserProfileUpdate (
    @Email String email,
    @NotBlank String firstName,
    String middleName,
    @NotBlank String lastName,
    String contactNumber
){}
