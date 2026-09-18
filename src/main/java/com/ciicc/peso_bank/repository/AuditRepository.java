package com.ciicc.peso_bank.repository;

import com.ciicc.peso_bank.entity.Audit;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuditRepository extends JpaRepository<Audit, Long>{
    @Query("SELECT DISTINCT a FROM Audit a LEFT JOIN a.user")
    List<Audit> findAllWithAudits();

    @Query("SELECT a FROM Audit a LEFT JOIN a.user WHERE a.auditId = :id")
    Optional<Audit> findWithAuditById(Long id);

    @Query("SELECT a FROM Audit a WHERE a.user.userId = :userId ORDER BY a.createdAt DESC")
    List<Audit> findByUserIdOrderByCreatedAtDesc(Long userId);
}
