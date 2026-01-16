package com.example.multitenant.application;

import com.example.multitenant.audit.AuditService;
import com.example.multitenant.domain.Tenant;
import com.example.multitenant.domain.TenantMembership;
import com.example.multitenant.domain.TenantRole;
import com.example.multitenant.domain.TenantSetting;
import com.example.multitenant.domain.UserAccount;
import com.example.multitenant.infrastructure.persistence.TenantMembershipRepository;
import com.example.multitenant.infrastructure.persistence.TenantRepository;
import com.example.multitenant.infrastructure.persistence.TenantSettingRepository;
import com.example.multitenant.infrastructure.persistence.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TenantAdminService {
  private final TenantRepository tenantRepository;
  private final TenantSettingRepository tenantSettingRepository;
  private final UserRepository userRepository;
  private final TenantMembershipRepository membershipRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuditService auditService;

  public TenantAdminService(TenantRepository tenantRepository,
                            TenantSettingRepository tenantSettingRepository,
                            UserRepository userRepository,
                            TenantMembershipRepository membershipRepository,
                            PasswordEncoder passwordEncoder,
                            AuditService auditService) {
    this.tenantRepository = tenantRepository;
    this.tenantSettingRepository = tenantSettingRepository;
    this.userRepository = userRepository;
    this.membershipRepository = membershipRepository;
    this.passwordEncoder = passwordEncoder;
    this.auditService = auditService;
  }

  public Tenant createTenant(String key, String name, String themeColor, String ownerEmail, String ownerName, String ownerPassword) {
    if (tenantRepository.findByKey(key).isPresent()) {
      throw new IllegalArgumentException("Tenant key already exists");
    }
    UUID tenantId = UUID.randomUUID();
    Tenant tenant = new Tenant(tenantId, key, name, themeColor, Instant.now());
    tenantRepository.save(tenant);

    TenantSetting settings = new TenantSetting(tenantId, name, themeColor, "{}", 120, Instant.now());
    tenantSettingRepository.save(settings);

    UserAccount owner = userRepository.findByEmail(ownerEmail)
        .orElseGet(() -> new UserAccount(UUID.randomUUID(), ownerEmail,
            passwordEncoder.encode(ownerPassword), ownerName, Instant.now()));
    userRepository.save(owner);

    membershipRepository.save(new TenantMembership(UUID.randomUUID(), tenantId, owner.getId(), TenantRole.OWNER, Instant.now()));
    auditService.record(owner.getId(), "TENANT_CREATED", "{\"tenantId\":\"" + tenantId + "\"}");
    return tenant;
  }

  public TenantMembership inviteUser(UUID tenantId, String email, String displayName, String password, TenantRole role) {
    Tenant tenant = tenantRepository.findById(tenantId)
        .orElseThrow(() -> new IllegalArgumentException("Tenant not found"));

    UserAccount user = userRepository.findByEmail(email)
        .orElseGet(() -> new UserAccount(UUID.randomUUID(), email,
            passwordEncoder.encode(password), displayName, Instant.now()));
    userRepository.save(user);

    Optional<TenantMembership> existing = membershipRepository.findByUserIdAndTenantId(user.getId(), tenant.getId());
    if (existing.isPresent()) {
      return existing.get();
    }

    TenantMembership membership = new TenantMembership(UUID.randomUUID(), tenant.getId(), user.getId(), role, Instant.now());
    TenantMembership saved = membershipRepository.save(membership);
    auditService.record(user.getId(), "USER_INVITED", "{\"tenantId\":\"" + tenant.getId() + "\"}");
    return saved;
  }
}
