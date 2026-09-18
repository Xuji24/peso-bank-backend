package com.ciicc.peso_bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserProfileDto (
    @NotBlank @Email String email,
    @NotBlank String firstName, 
    String middleName,
    @NotBlank String lastName,
    String contactNumber
){}
