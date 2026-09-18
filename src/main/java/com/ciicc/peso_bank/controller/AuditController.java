package com.ciicc.peso_bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciicc.peso_bank.dto.AuditDto;
import com.ciicc.peso_bank.service.AuditService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/user/{userId}")
    public List<AuditDto> getTrailForUser(@PathVariable Long userId) {
        return auditService.getTrailForUser(userId);
    }
}
