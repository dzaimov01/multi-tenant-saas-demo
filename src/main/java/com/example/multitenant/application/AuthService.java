package com.example.multitenant.application;

import com.example.multitenant.domain.TenantMembership;
import com.example.multitenant.infrastructure.persistence.TenantMembershipRepository;
import com.example.multitenant.infrastructure.persistence.UserRepository;
import com.example.multitenant.security.JwtService;
import com.example.multitenant.security.UserPrincipal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final TenantMembershipRepository membershipRepository;
  private final UserRepository userRepository;

  public AuthService(AuthenticationManager authenticationManager,
                     JwtService jwtService,
                     TenantMembershipRepository membershipRepository,
                     UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
  }

  public AuthResult login(String email, String password, UUID tenantId) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password));
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    List<TenantMembership> memberships = membershipRepository.findByUserId(principal.getUserId());
    if (memberships.isEmpty()) {
      throw new IllegalStateException("User has no tenant memberships");
    }

    UUID resolvedTenantId = tenantId;
    if (resolvedTenantId == null && memberships.size() == 1) {
      resolvedTenantId = memberships.get(0).getTenantId();
    }
    if (resolvedTenantId == null) {
      throw new IllegalArgumentException("Tenant selection required");
    }

    TenantMembership membership = membershipRepository.findByUserIdAndTenantId(principal.getUserId(), resolvedTenantId)
        .orElseThrow(() -> new IllegalArgumentException("User not in tenant"));

    String token = jwtService.generateToken(principal.getUserId(), principal.getUsername(), resolvedTenantId,
        membership.getRole().name());

    String displayName = userRepository.findById(principal.getUserId())
        .map(user -> user.getDisplayName())
        .orElse(principal.getUsername());

    return new AuthResult(token, principal.getUserId(), displayName, resolvedTenantId, membership.getRole().name());
  }
}
