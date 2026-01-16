package com.example.multitenant.api;

import com.example.multitenant.domain.AuditLog;
import com.example.multitenant.domain.TenantRole;
import com.example.multitenant.infrastructure.persistence.AuditLogRepository;
import com.example.multitenant.security.TenantAuthorizationService;
import com.example.multitenant.tenant.TenantContextService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
  private final AuditLogRepository auditLogRepository;
  private final TenantContextService tenantContextService;
  private final TenantAuthorizationService authorizationService;

  public AuditController(AuditLogRepository auditLogRepository,
                         TenantContextService tenantContextService,
                         TenantAuthorizationService authorizationService) {
    this.auditLogRepository = auditLogRepository;
    this.tenantContextService = tenantContextService;
    this.authorizationService = authorizationService;
  }

  @GetMapping
  public List<AuditResponse> list() {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN, TenantRole.MEMBER);
    UUID tenantId = tenantContextService.requireTenantId();
    return auditLogRepository.findAllByTenantId(tenantId)
        .stream()
        .map(AuditResponse::from)
        .toList();
  }

  public record AuditResponse(UUID id, UUID actorUserId, String action, String details, Instant createdAt) {
    public static AuditResponse from(AuditLog log) {
      return new AuditResponse(log.getId(), log.getActorUserId(), log.getAction(), log.getDetails(), log.getCreatedAt());
    }
  }
}
