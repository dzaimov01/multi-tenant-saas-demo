package com.example.multitenant.infrastructure.persistence;

import com.example.multitenant.domain.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
  List<Project> findAllByTenantId(UUID tenantId);

  Optional<Project> findByIdAndTenantId(UUID id, UUID tenantId);
}
