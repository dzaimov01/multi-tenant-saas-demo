package com.example.multitenant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey extends TenantScopedEntity {

  @Id
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "key_hash", nullable = false)
  private String keyHash;

  @Column(name = "last4", nullable = false)
  private String last4;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  protected ApiKey() {}

  public ApiKey(UUID id, String name, String keyHash, String last4, Instant createdAt) {
    this.id = id;
    this.name = name;
    this.keyHash = keyHash;
    this.last4 = last4;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getKeyHash() {
    return keyHash;
  }

  public String getLast4() {
    return last4;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getRevokedAt() {
    return revokedAt;
  }

  public void revoke(Instant revokedAt) {
    this.revokedAt = revokedAt;
  }
}
