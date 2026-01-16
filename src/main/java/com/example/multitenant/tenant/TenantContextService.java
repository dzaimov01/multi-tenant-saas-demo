package com.example.multitenant.tenant;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TenantContextService {
  public UUID requireTenantId() {
    return TenantContextHolder.getCurrent()
        .map(TenantContext::getTenantId)
        .orElseThrow(() -> new IllegalStateException("Tenant context required"));
  }
}
