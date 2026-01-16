package com.example.multitenant.tenant;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TenantResolutionService {
  private final List<TenantResolver> resolvers;

  public TenantResolutionService(List<TenantResolver> resolvers) {
    this.resolvers = resolvers;
  }

  public Optional<TenantResolution> resolve(HttpServletRequest request) {
    for (TenantResolver resolver : resolvers) {
      Optional<TenantResolution> resolved = resolver.resolve(request);
      if (resolved.isPresent()) {
        return resolved;
      }
    }
    return Optional.empty();
  }
}
