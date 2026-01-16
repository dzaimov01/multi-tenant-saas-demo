package com.example.multitenant.api;

import com.example.multitenant.application.ApiKeyService;
import com.example.multitenant.domain.ApiKey;
import com.example.multitenant.domain.TenantRole;
import com.example.multitenant.security.TenantAuthorizationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/api-keys")
public class ApiKeyController {
  private final ApiKeyService apiKeyService;
  private final TenantAuthorizationService authorizationService;

  public ApiKeyController(ApiKeyService apiKeyService, TenantAuthorizationService authorizationService) {
    this.apiKeyService = apiKeyService;
    this.authorizationService = authorizationService;
  }

  @GetMapping
  public List<ApiKeyResponse> list() {
    return apiKeyService.listKeys().stream().map(ApiKeyResponse::from).toList();
  }

  @PostMapping
  public ApiKeyIssueResponse create(@Valid @RequestBody ApiKeyRequest request) {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN);
    ApiKeyService.ApiKeyIssue issue = apiKeyService.issueKey(request.name());
    return new ApiKeyIssueResponse(ApiKeyResponse.from(issue.apiKey()), issue.rawKey());
  }

  @DeleteMapping("/{keyId}")
  public void revoke(@PathVariable UUID keyId) {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN);
    apiKeyService.revoke(keyId);
  }

  public record ApiKeyRequest(@NotBlank String name) {}

  public record ApiKeyIssueResponse(ApiKeyResponse key, String rawKey) {}

  public record ApiKeyResponse(UUID id, String name, String last4, Instant createdAt, Instant revokedAt) {
    public static ApiKeyResponse from(ApiKey apiKey) {
      return new ApiKeyResponse(apiKey.getId(), apiKey.getName(), apiKey.getLast4(),
          apiKey.getCreatedAt(), apiKey.getRevokedAt());
    }
  }
}
