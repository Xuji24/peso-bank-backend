package com.ciicc.peso_bank.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ciicc.peso_bank.dto.AuditDto;
import com.ciicc.peso_bank.entity.Audit;
import com.ciicc.peso_bank.entity.User;
import com.ciicc.peso_bank.mapper.AuditMapper;
import com.ciicc.peso_bank.repository.AuditRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    @Transactional
    public void log(User user, String action) {
        Audit audit = new Audit();
        audit.setUser(user);
        audit.setActions(action);
        audit.setCreatedAt(LocalDateTime.now());
        auditRepository.save(audit);
    }

    @Transactional(readOnly = true)
    public List<AuditDto> getTrailForUser(Long userId) {
        return auditRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(auditMapper::toDto)
            .toList();
    }
}
