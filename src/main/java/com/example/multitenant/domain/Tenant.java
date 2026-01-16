package com.example.multitenant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class Tenant {

  @Id
  private UUID id;

  @Column(name = "tenant_key", nullable = false, unique = true)
  private String key;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "theme_color", nullable = false)
  private String themeColor;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Tenant() {}

  public Tenant(UUID id, String key, String name, String themeColor, Instant createdAt) {
    this.id = id;
    this.key = key;
    this.name = name;
    this.themeColor = themeColor;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getThemeColor() {
    return themeColor;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
