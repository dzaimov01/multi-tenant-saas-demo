package com.example.multitenant.tenant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.multitenant.domain.Project;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TenantGuardTest {

  @AfterEach
  void cleanup() {
    TenantContextHolder.clear();
  }

  @Test
  void guardBlocksCrossTenantLoad() {
    UUID tenantA = UUID.randomUUID();
    UUID tenantB = UUID.randomUUID();
    TenantContextHolder.set(new TenantContext(tenantA, "acme", TenantResolutionMethod.HEADER));

    Project project = new Project(UUID.randomUUID(), "Demo", "", Instant.now());
    project.setTenantId(tenantB);

    TenantEntityListener listener = new TenantEntityListener();

    assertThatThrownBy(() -> listener.verifyTenant(project))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cross-tenant");
  }
}
