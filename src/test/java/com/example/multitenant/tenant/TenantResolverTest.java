package com.example.multitenant.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.multitenant.domain.Tenant;
import com.example.multitenant.infrastructure.persistence.TenantRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

class TenantResolverTest {
  private TenantRepository tenantRepository;
  private TenantLookupService tenantLookupService;

  @BeforeEach
  void setup() {
    tenantRepository = Mockito.mock(TenantRepository.class);
    tenantLookupService = new TenantLookupService(tenantRepository);
  }

  @Test
  void resolvesTenantFromHeader() {
    UUID tenantId = UUID.randomUUID();
    Tenant tenant = new Tenant(tenantId, "acme", "Acme", "#111", Instant.now());
    Mockito.when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

    HeaderTenantResolver resolver = new HeaderTenantResolver(tenantLookupService, "X-Tenant-Id");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("X-Tenant-Id", tenantId.toString());

    Optional<TenantResolution> resolution = resolver.resolve(request);

    assertThat(resolution).isPresent();
    assertThat(resolution.get().tenantId()).isEqualTo(tenantId);
    assertThat(resolution.get().method()).isEqualTo(TenantResolutionMethod.HEADER);
  }

  @Test
  void resolvesTenantFromSubdomain() {
    UUID tenantId = UUID.randomUUID();
    Tenant tenant = new Tenant(tenantId, "acme", "Acme", "#111", Instant.now());
    Mockito.when(tenantRepository.findByKey("acme")).thenReturn(Optional.of(tenant));

    SubdomainTenantResolver resolver = new SubdomainTenantResolver(tenantLookupService, "localhost");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServerName("acme.localhost");

    Optional<TenantResolution> resolution = resolver.resolve(request);

    assertThat(resolution).isPresent();
    assertThat(resolution.get().tenantId()).isEqualTo(tenantId);
    assertThat(resolution.get().method()).isEqualTo(TenantResolutionMethod.SUBDOMAIN);
  }
}
