package com.example.multitenant.api;

import com.example.multitenant.application.TenantAdminService;
import com.example.multitenant.application.TenantMetricsService;
import com.example.multitenant.domain.Tenant;
import com.example.multitenant.domain.TenantMembership;
import com.example.multitenant.domain.TenantRole;
import com.example.multitenant.security.TenantAuthorizationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  private final TenantAdminService tenantAdminService;
  private final TenantAuthorizationService authorizationService;
  private final TenantMetricsService tenantMetricsService;

  public AdminController(TenantAdminService tenantAdminService,
                         TenantAuthorizationService authorizationService,
                         TenantMetricsService tenantMetricsService) {
    this.tenantAdminService = tenantAdminService;
    this.authorizationService = authorizationService;
    this.tenantMetricsService = tenantMetricsService;
  }

  @PostMapping("/tenants")
  public TenantResponse createTenant(@Valid @RequestBody CreateTenantRequest request) {
    authorizationService.requireRole(TenantRole.OWNER);
    Tenant tenant = tenantAdminService.createTenant(request.key(), request.name(), request.themeColor(),
        request.ownerEmail(), request.ownerName(), request.ownerPassword());
    return new TenantResponse(tenant.getId(), tenant.getKey(), tenant.getName(), tenant.getThemeColor());
  }

  @PostMapping("/tenants/{tenantId}/invites")
  public InviteResponse invite(@PathVariable UUID tenantId, @Valid @RequestBody InviteRequest request) {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN);
    TenantMembership membership = tenantAdminService.inviteUser(tenantId, request.email(), request.displayName(),
        request.password(), request.role());
    return new InviteResponse(membership.getUserId(), membership.getRole().name());
  }

  @GetMapping("/metrics")
  public MetricsResponse metrics() {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN);
    Map<UUID, Long> requests = tenantMetricsService.snapshotRequests();
    Map<UUID, Long> activeUsers = tenantMetricsService.snapshotActiveUsers();
    return new MetricsResponse(requests, activeUsers);
  }

  public record CreateTenantRequest(@NotBlank String key,
                                    @NotBlank String name,
                                    @NotBlank String themeColor,
                                    @Email @NotBlank String ownerEmail,
                                    @NotBlank String ownerName,
                                    @NotBlank String ownerPassword) {}

  public record TenantResponse(UUID id, String key, String name, String themeColor) {}

  public record InviteRequest(@Email @NotBlank String email,
                              @NotBlank String displayName,
                              @NotBlank String password,
                              TenantRole role) {}

  public record InviteResponse(UUID userId, String role) {}

  public record MetricsResponse(Map<UUID, Long> requestsPerTenant, Map<UUID, Long> activeUsersPerTenant) {}
}
