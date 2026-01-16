package com.example.multitenant.tenant;

import java.util.Optional;

public final class TenantContextHolder {
  private static final ThreadLocal<TenantContext> CURRENT = new ThreadLocal<>();

  private TenantContextHolder() {}

  public static Optional<TenantContext> getCurrent() {
    return Optional.ofNullable(CURRENT.get());
  }

  public static void set(TenantContext context) {
    CURRENT.set(context);
  }

  public static void clear() {
    CURRENT.remove();
  }
}
