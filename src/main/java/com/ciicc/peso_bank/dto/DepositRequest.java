package com.ciicc.peso_bank.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DepositRequest(
    @NotNull Long accountId,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount
) {}
