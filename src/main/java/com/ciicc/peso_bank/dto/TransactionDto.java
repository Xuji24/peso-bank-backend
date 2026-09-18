package com.ciicc.peso_bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(
    Long transactionId,
    Long accountId,
    Long receiverAccountId,
    BigDecimal amount,
    String transactionType,
    LocalDateTime transactionDate
) {}
