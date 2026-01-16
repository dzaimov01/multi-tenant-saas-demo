package com.example.multitenant.infrastructure.persistence;

import com.example.multitenant.domain.TenantSetting;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantSettingRepository extends JpaRepository<TenantSetting, UUID> {}
