package com.example.multitenant.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantContextFilter extends OncePerRequestFilter {
  private static final String CORRELATION_HEADER = "X-Correlation-Id";

  private final TenantResolutionService tenantResolutionService;

  public TenantContextFilter(TenantResolutionService tenantResolutionService) {
    this.tenantResolutionService = tenantResolutionService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String correlationId = Optional.ofNullable(request.getHeader(CORRELATION_HEADER))
        .filter(value -> !value.isBlank())
        .orElseGet(() -> UUID.randomUUID().toString());

    MDC.put("correlationId", correlationId);
    response.setHeader(CORRELATION_HEADER, correlationId);

    tenantResolutionService.resolve(request)
        .ifPresent(resolution -> {
          TenantContext context = new TenantContext(resolution.tenantId(), resolution.tenantKey(), resolution.method());
          TenantContextHolder.set(context);
          MDC.put("tenantId", resolution.tenantId().toString());
        });

    try {
      filterChain.doFilter(request, response);
    } finally {
      TenantContextHolder.clear();
      MDC.remove("tenantId");
      MDC.remove("correlationId");
    }
  }
}
