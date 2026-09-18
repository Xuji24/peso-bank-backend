package com.ciicc.peso_bank.dto;

import java.time.LocalDateTime;

public record AuditDto(
    Long auditId,
    String actions,
    LocalDateTime createdAt,
    Long userId
) {}
