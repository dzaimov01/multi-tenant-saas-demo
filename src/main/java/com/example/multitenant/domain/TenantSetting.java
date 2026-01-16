package com.example.multitenant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_settings")
public class TenantSetting {

  @Id
  @Column(name = "tenant_id")
  private UUID tenantId;

  @Column(name = "branding_name", nullable = false)
  private String brandingName;

  @Column(name = "theme_color", nullable = false)
  private String themeColor;

  @Column(name = "feature_flags", columnDefinition = "jsonb", nullable = false)
  private String featureFlags;

  @Column(name = "rate_limit_per_min", nullable = false)
  private int rateLimitPerMin;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected TenantSetting() {}

  public TenantSetting(UUID tenantId, String brandingName, String themeColor, String featureFlags, int rateLimitPerMin, Instant updatedAt) {
    this.tenantId = tenantId;
    this.brandingName = brandingName;
    this.themeColor = themeColor;
    this.featureFlags = featureFlags;
    this.rateLimitPerMin = rateLimitPerMin;
    this.updatedAt = updatedAt;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public String getBrandingName() {
    return brandingName;
  }

  public String getThemeColor() {
    return themeColor;
  }

  public String getFeatureFlags() {
    return featureFlags;
  }

  public int getRateLimitPerMin() {
    return rateLimitPerMin;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
