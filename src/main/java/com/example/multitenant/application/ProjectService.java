package com.example.multitenant.application;

import com.example.multitenant.audit.AuditService;
import com.example.multitenant.domain.Project;
import com.example.multitenant.infrastructure.persistence.ProjectRepository;
import com.example.multitenant.security.UserPrincipal;
import com.example.multitenant.tenant.TenantContextService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final TenantContextService tenantContextService;
  private final AuditService auditService;

  public ProjectService(ProjectRepository projectRepository,
                        TenantContextService tenantContextService,
                        AuditService auditService) {
    this.projectRepository = projectRepository;
    this.tenantContextService = tenantContextService;
    this.auditService = auditService;
  }

  public List<Project> list() {
    UUID tenantId = tenantContextService.requireTenantId();
    return projectRepository.findAllByTenantId(tenantId);
  }

  public Project create(String name, String description) {
    Project project = new Project(UUID.randomUUID(), name, description, Instant.now());
    project.setTenantId(tenantContextService.requireTenantId());
    Project saved = projectRepository.save(project);
    auditService.record(currentUserId(), "PROJECT_CREATED", "{\"projectId\":\"" + saved.getId() + "\"}");
    return saved;
  }

  public Project update(UUID projectId, String name, String description) {
    UUID tenantId = tenantContextService.requireTenantId();
    Project project = projectRepository.findByIdAndTenantId(projectId, tenantId)
        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    project.update(name, description);
    Project saved = projectRepository.save(project);
    auditService.record(currentUserId(), "PROJECT_UPDATED", "{\"projectId\":\"" + saved.getId() + "\"}");
    return saved;
  }

  public void delete(UUID projectId) {
    UUID tenantId = tenantContextService.requireTenantId();
    Project project = projectRepository.findByIdAndTenantId(projectId, tenantId)
        .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    projectRepository.delete(project);
    auditService.record(currentUserId(), "PROJECT_DELETED", "{\"projectId\":\"" + projectId + "\"}");
  }

  private UUID currentUserId() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
      return principal.getUserId();
    }
    return null;
  }
}
