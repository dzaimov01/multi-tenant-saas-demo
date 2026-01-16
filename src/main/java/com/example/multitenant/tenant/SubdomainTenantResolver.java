package com.example.multitenant.tenant;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class SubdomainTenantResolver implements TenantResolver {
  private final TenantLookupService tenantLookupService;
  private final String baseDomain;

  public SubdomainTenantResolver(TenantLookupService tenantLookupService,
                                 @Value("${app.tenant.base-domain:localhost}") String baseDomain) {
    this.tenantLookupService = tenantLookupService;
    this.baseDomain = baseDomain;
  }

  @Override
  public Optional<TenantResolution> resolve(HttpServletRequest request) {
    String host = Optional.ofNullable(request.getHeader("X-Forwarded-Host"))
        .orElse(request.getHeader("Host"));
    if (host == null || host.isBlank()) {
      host = request.getServerName();
    }
    if (host == null) {
      return Optional.empty();
    }
    host = host.split(":")[0];
    if (!host.endsWith(baseDomain)) {
      return Optional.empty();
    }
    String trimmed = host.replaceFirst("\\." + baseDomain + "$", "");
    if (trimmed.equals(host) || trimmed.isBlank()) {
      return Optional.empty();
    }
    String tenantKey = trimmed.toLowerCase();
    return tenantLookupService.findByKey(tenantKey)
        .map(tenant -> new TenantResolution(tenant.getId(), tenant.getKey(), TenantResolutionMethod.SUBDOMAIN));
  }
}
