package com.example.multitenant.infrastructure.persistence;

import com.example.multitenant.domain.AuditLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
  List<AuditLog> findAllByTenantId(UUID tenantId);
}
