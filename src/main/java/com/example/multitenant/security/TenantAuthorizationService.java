package com.example.multitenant.security;

import com.example.multitenant.domain.TenantRole;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TenantAuthorizationService {

  public void requireRole(TenantRole... roles) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new IllegalStateException("Authentication required");
    }
    Set<String> allowed = Arrays.stream(roles)
        .map(role -> "ROLE_" + role.name())
        .collect(Collectors.toSet());
    boolean allowedRole = authentication.getAuthorities().stream()
        .anyMatch(authority -> allowed.contains(authority.getAuthority()));
    if (!allowedRole) {
      throw new IllegalStateException("Insufficient role");
    }
  }
}
