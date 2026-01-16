package com.example.multitenant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends TenantScopedEntity {

  @Id
  private UUID id;

  @Column(name = "actor_user_id")
  private UUID actorUserId;

  @Column(name = "action", nullable = false)
  private String action;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "details", columnDefinition = "jsonb")
  private String details;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected AuditLog() {}

  public AuditLog(UUID id, UUID actorUserId, String action, String details, Instant createdAt) {
    this.id = id;
    this.actorUserId = actorUserId;
    this.action = action;
    this.details = details;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getActorUserId() {
    return actorUserId;
  }

  public String getAction() {
    return action;
  }

  public String getDetails() {
    return details;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
