package com.example.multitenant.tenant;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class HeaderTenantResolver implements TenantResolver {
  private final TenantLookupService tenantLookupService;
  private final String headerName;

  public HeaderTenantResolver(TenantLookupService tenantLookupService,
                              @Value("${app.tenant.header:X-Tenant-Id}") String headerName) {
    this.tenantLookupService = tenantLookupService;
    this.headerName = headerName;
  }

  @Override
  public Optional<TenantResolution> resolve(HttpServletRequest request) {
    String header = request.getHeader(headerName);
    if (header == null || header.isBlank()) {
      return Optional.empty();
    }
    try {
      UUID tenantId = UUID.fromString(header.trim());
      return tenantLookupService.findById(tenantId)
          .map(tenant -> new TenantResolution(tenant.getId(), tenant.getKey(), TenantResolutionMethod.HEADER));
    } catch (IllegalArgumentException ex) {
      return Optional.empty();
    }
  }
}
