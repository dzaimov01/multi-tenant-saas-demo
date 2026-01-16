package com.example.multitenant.tenant;

import com.example.multitenant.domain.TenantOwned;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import java.util.UUID;

public class TenantEntityListener {

  @PrePersist
  public void assignTenant(Object entity) {
    if (!(entity instanceof TenantOwned tenantOwned)) {
      return;
    }
    if (tenantOwned.getTenantId() != null) {
      return;
    }
    TenantContextHolder.getCurrent().ifPresent(context -> tenantOwned.setTenantId(context.getTenantId()));
  }

  @PostLoad
  public void verifyTenant(Object entity) {
    if (!(entity instanceof TenantOwned tenantOwned)) {
      return;
    }
    TenantContextHolder.getCurrent().ifPresent(context -> {
      UUID tenantId = tenantOwned.getTenantId();
      if (tenantId != null && !tenantId.equals(context.getTenantId())) {
        throw new IllegalStateException("Cross-tenant entity load blocked by guard");
      }
    });
  }
}
