package com.example.multitenant.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.multitenant.api.AuthController.LoginRequest;
import com.example.multitenant.application.AuthResult;
import com.example.multitenant.domain.Tenant;
import com.example.multitenant.domain.TenantMembership;
import com.example.multitenant.domain.TenantRole;
import com.example.multitenant.domain.UserAccount;
import com.example.multitenant.infrastructure.persistence.TenantMembershipRepository;
import com.example.multitenant.infrastructure.persistence.TenantRepository;
import com.example.multitenant.infrastructure.persistence.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantIsolationIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
      .withDatabaseName("saas_demo")
      .withUsername("saas")
      .withPassword("saas");

  static {
    postgres.start();
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TenantRepository tenantRepository;

  @Autowired
  private TenantMembershipRepository membershipRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  private UUID tenantA;
  private UUID tenantB;
  private UserAccount user;

  @BeforeEach
  void setup() {
    membershipRepository.deleteAll();
    tenantRepository.deleteAll();
    userRepository.deleteAll();

    tenantA = UUID.randomUUID();
    tenantB = UUID.randomUUID();

    tenantRepository.saveAll(List.of(
        new Tenant(tenantA, "acme", "Acme", "#111", Instant.now()),
        new Tenant(tenantB, "globex", "Globex", "#222", Instant.now())
    ));

    user = new UserAccount(UUID.randomUUID(), "shared@example.com", passwordEncoder.encode("Password123!"),
        "Shared User", Instant.now());
    userRepository.save(user);

    membershipRepository.saveAll(List.of(
        new TenantMembership(UUID.randomUUID(), tenantA, user.getId(), TenantRole.OWNER, Instant.now()),
        new TenantMembership(UUID.randomUUID(), tenantB, user.getId(), TenantRole.OWNER, Instant.now())
    ));
  }

  @Test
  void headerTenantIsolationIsEnforced() {
    AuthResult authA = loginForTenant(tenantA);
    AuthResult authB = loginForTenant(tenantB);

    HttpHeaders headersA = new HttpHeaders();
    headersA.setBearerAuth(authA.token());
    headersA.set("X-Tenant-Id", tenantA.toString());
    headersA.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/projects"), new HttpEntity<>(Map.of("name", "Alpha"), headersA), Map.class);
    assertThat(created.getStatusCode().is2xxSuccessful()).isTrue();

    HttpHeaders headersB = new HttpHeaders();
    headersB.setBearerAuth(authB.token());
    headersB.set("X-Tenant-Id", tenantB.toString());

    ResponseEntity<List> listB = restTemplate.exchange(
        url("/api/projects"), HttpMethod.GET, new HttpEntity<>(headersB), List.class);
    assertThat(listB.getBody()).isEmpty();
  }

  @Test
  void subdomainTenantIsolationIsEnforced() {
    AuthResult authA = loginForTenant(tenantA);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(authA.token());
    headers.set("X-Forwarded-Host", "acme.localhost");
    headers.setContentType(MediaType.APPLICATION_JSON);

    ResponseEntity<Map> created = restTemplate.postForEntity(
        url("/api/projects"), new HttpEntity<>(Map.of("name", "Beta"), headers), Map.class);
    assertThat(created.getStatusCode().is2xxSuccessful()).isTrue();

    HttpHeaders headersOther = new HttpHeaders();
    headersOther.setBearerAuth(authA.token());
    headersOther.set("X-Forwarded-Host", "globex.localhost");

    ResponseEntity<List> listOther = restTemplate.exchange(
        url("/api/projects"), HttpMethod.GET, new HttpEntity<>(headersOther), List.class);
    assertThat(listOther.getStatusCode().is4xxClientError()).isTrue();
  }

  private AuthResult loginForTenant(UUID tenantId) {
    ResponseEntity<AuthResult> response = restTemplate.postForEntity(
        url("/api/auth/login"), new LoginRequest("shared@example.com", "Password123!", tenantId), AuthResult.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    return response.getBody();
  }

  private String url(String path) {
    return "http://localhost:" + port + path;
  }
}
