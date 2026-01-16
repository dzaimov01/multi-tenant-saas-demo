package com.example.multitenant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_memberships")
public class TenantMembership {

  @Id
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private TenantRole role;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected TenantMembership() {}

  public TenantMembership(UUID id, UUID tenantId, UUID userId, TenantRole role, Instant createdAt) {
    this.id = id;
    this.tenantId = tenantId;
    this.userId = userId;
    this.role = role;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getUserId() {
    return userId;
  }

  public TenantRole getRole() {
    return role;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
