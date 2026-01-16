package com.example.multitenant.domain;

import java.util.UUID;

public interface TenantOwned {
  UUID getTenantId();

  void setTenantId(UUID tenantId);
}
