package com.example.multitenant.tenant;

import java.util.UUID;

public final class TenantContext {
  private final UUID tenantId;
  private final String tenantKey;
  private final TenantResolutionMethod method;

  public TenantContext(UUID tenantId, String tenantKey, TenantResolutionMethod method) {
    this.tenantId = tenantId;
    this.tenantKey = tenantKey;
    this.method = method;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public String getTenantKey() {
    return tenantKey;
  }

  public TenantResolutionMethod getMethod() {
    return method;
  }
}
