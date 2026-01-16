package com.example.multitenant.application;

import com.example.multitenant.audit.AuditService;
import com.example.multitenant.domain.ApiKey;
import com.example.multitenant.infrastructure.persistence.ApiKeyRepository;
import com.example.multitenant.tenant.TenantContextService;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {
  private final ApiKeyRepository apiKeyRepository;
  private final TenantContextService tenantContextService;
  private final PasswordEncoder passwordEncoder;
  private final AuditService auditService;
  private final SecureRandom secureRandom = new SecureRandom();

  public ApiKeyService(ApiKeyRepository apiKeyRepository,
                       TenantContextService tenantContextService,
                       PasswordEncoder passwordEncoder,
                       AuditService auditService) {
    this.apiKeyRepository = apiKeyRepository;
    this.tenantContextService = tenantContextService;
    this.passwordEncoder = passwordEncoder;
    this.auditService = auditService;
  }

  public ApiKeyIssue issueKey(String name) {
    UUID tenantId = tenantContextService.requireTenantId();
    String rawKey = generateRawKey();
    String hash = passwordEncoder.encode(rawKey);
    String last4 = rawKey.substring(rawKey.length() - 4);
    ApiKey apiKey = new ApiKey(UUID.randomUUID(), name, hash, last4, Instant.now());
    apiKey.setTenantId(tenantId);
    apiKeyRepository.save(apiKey);
    auditService.record(currentUserId(), "API_KEY_ISSUED", "{\"keyId\":\"" + apiKey.getId() + "\"}");
    return new ApiKeyIssue(apiKey, rawKey);
  }

  public List<ApiKey> listKeys() {
    return apiKeyRepository.findAllByTenantId(tenantContextService.requireTenantId());
  }

  public void revoke(UUID keyId) {
    ApiKey apiKey = apiKeyRepository.findByIdAndTenantId(keyId, tenantContextService.requireTenantId())
        .orElseThrow(() -> new IllegalArgumentException("API key not found"));
    apiKey.revoke(Instant.now());
    apiKeyRepository.save(apiKey);
    auditService.record(currentUserId(), "API_KEY_REVOKED", "{\"keyId\":\"" + apiKey.getId() + "\"}");
  }

  private String generateRawKey() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  public record ApiKeyIssue(ApiKey apiKey, String rawKey) {}

  private UUID currentUserId() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof com.example.multitenant.security.UserPrincipal principal) {
      return principal.getUserId();
    }
    return null;
  }
}
