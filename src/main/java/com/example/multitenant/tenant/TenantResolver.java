package com.example.multitenant.tenant;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface TenantResolver {
  Optional<TenantResolution> resolve(HttpServletRequest request);
}
