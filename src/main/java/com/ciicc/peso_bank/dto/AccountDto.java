package com.ciicc.peso_bank.dto;

import java.math.BigDecimal;

public record AccountDto(
    Long accountId,
    Long accountNumber,
    BigDecimal balance,
    BigDecimal debt,
    String accountType,
    boolean active,
    Long userId
) {}
