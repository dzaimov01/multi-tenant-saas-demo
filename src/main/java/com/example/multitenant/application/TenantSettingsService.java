package com.example.multitenant.application;

import com.example.multitenant.domain.TenantSetting;
import com.example.multitenant.infrastructure.persistence.TenantSettingRepository;
import com.example.multitenant.tenant.TenantContextService;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TenantSettingsService {
  private final TenantSettingRepository tenantSettingRepository;
  private final TenantContextService tenantContextService;

  public TenantSettingsService(TenantSettingRepository tenantSettingRepository, TenantContextService tenantContextService) {
    this.tenantSettingRepository = tenantSettingRepository;
    this.tenantContextService = tenantContextService;
  }

  @Cacheable(cacheNames = "tenantSettings", key = "#tenantId")
  public TenantSetting getSettings(UUID tenantId) {
    return tenantSettingRepository.findById(tenantId)
        .orElseThrow(() -> new IllegalArgumentException("Tenant settings not found"));
  }

  public TenantSetting getCurrentTenantSettings() {
    UUID tenantId = tenantContextService.requireTenantId();
    return getSettings(tenantId);
  }
}
