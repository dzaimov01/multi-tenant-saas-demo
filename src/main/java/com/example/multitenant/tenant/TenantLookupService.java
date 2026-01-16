package com.example.multitenant.tenant;

import com.example.multitenant.domain.Tenant;
import com.example.multitenant.infrastructure.persistence.TenantRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TenantLookupService {
  private final TenantRepository tenantRepository;

  public TenantLookupService(TenantRepository tenantRepository) {
    this.tenantRepository = tenantRepository;
  }

  public Optional<Tenant> findById(UUID id) {
    return tenantRepository.findById(id);
  }

  public Optional<Tenant> findByKey(String key) {
    return tenantRepository.findByKey(key);
  }
}
