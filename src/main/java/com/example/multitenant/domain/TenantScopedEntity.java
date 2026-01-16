package com.example.multitenant.domain;

import com.example.multitenant.tenant.TenantEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(TenantEntityListener.class)
public abstract class TenantScopedEntity implements TenantOwned {

  @Column(name = "tenant_id", nullable = false, updatable = false)
  private UUID tenantId;

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  @Override
  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }
}
