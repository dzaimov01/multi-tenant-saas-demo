package com.example.multitenant.application;

import com.example.multitenant.security.UserPrincipal;
import com.example.multitenant.tenant.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantMetricsFilter extends OncePerRequestFilter {
  private final TenantMetricsService tenantMetricsService;

  public TenantMetricsFilter(TenantMetricsService tenantMetricsService) {
    this.tenantMetricsService = tenantMetricsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    TenantContextHolder.getCurrent().ifPresent(context -> {
      tenantMetricsService.recordRequest(context.getTenantId());
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
        tenantMetricsService.recordActiveUser(context.getTenantId());
      }
    });

    filterChain.doFilter(request, response);
  }
}
