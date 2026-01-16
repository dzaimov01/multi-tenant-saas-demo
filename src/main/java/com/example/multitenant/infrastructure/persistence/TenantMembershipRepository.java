package com.example.multitenant.infrastructure.persistence;

import com.example.multitenant.domain.TenantMembership;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantMembershipRepository extends JpaRepository<TenantMembership, UUID> {
  List<TenantMembership> findByUserId(UUID userId);

  Optional<TenantMembership> findByUserIdAndTenantId(UUID userId, UUID tenantId);
}
