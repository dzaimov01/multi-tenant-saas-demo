package com.example.multitenant.security;

import com.example.multitenant.tenant.TenantContext;
import com.example.multitenant.tenant.TenantContextHolder;
import com.example.multitenant.tenant.TenantResolutionMethod;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring("Bearer ".length());
    Claims claims = jwtService.parse(token);
    UUID userId = UUID.fromString(claims.getSubject());
    String email = claims.get("email", String.class);
    String role = claims.get("role", String.class);
    String tenantIdClaim = claims.get("tenantId", String.class);
    UUID tenantId = tenantIdClaim == null ? null : UUID.fromString(tenantIdClaim);

    Optional<TenantContext> currentContext = TenantContextHolder.getCurrent();
    if (tenantId != null && currentContext.isPresent()) {
      if (!tenantId.equals(currentContext.get().getTenantId())) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }

    if (tenantId != null && currentContext.isEmpty()) {
      TenantContextHolder.set(new TenantContext(tenantId, null, TenantResolutionMethod.TOKEN));
    }

    UserPrincipal principal = new UserPrincipal(userId, email, "");
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        principal, token, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}
