package com.example.multitenant.infrastructure.persistence;

import com.example.multitenant.domain.ApiKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
  List<ApiKey> findAllByTenantId(UUID tenantId);

  Optional<ApiKey> findByIdAndTenantId(UUID id, UUID tenantId);
}
