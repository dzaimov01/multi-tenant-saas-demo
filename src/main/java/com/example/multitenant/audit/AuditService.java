package com.example.multitenant.audit;

import com.example.multitenant.domain.AuditLog;
import com.example.multitenant.infrastructure.persistence.AuditLogRepository;
import com.example.multitenant.tenant.TenantContextService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
  private final AuditLogRepository auditLogRepository;
  private final TenantContextService tenantContextService;

  public AuditService(AuditLogRepository auditLogRepository, TenantContextService tenantContextService) {
    this.auditLogRepository = auditLogRepository;
    this.tenantContextService = tenantContextService;
  }

  public void record(UUID actorUserId, String action, String detailsJson) {
    UUID tenantId = tenantContextService.requireTenantId();
    AuditLog log = new AuditLog(UUID.randomUUID(), actorUserId, action, detailsJson, Instant.now());
    log.setTenantId(tenantId);
    auditLogRepository.save(log);
  }
}
