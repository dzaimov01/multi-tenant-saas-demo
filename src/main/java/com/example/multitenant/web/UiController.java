package com.example.multitenant.web;

import com.example.multitenant.application.ProjectService;
import com.example.multitenant.application.TenantSettingsService;
import com.example.multitenant.domain.Project;
import com.example.multitenant.domain.Tenant;
import com.example.multitenant.infrastructure.persistence.TenantMembershipRepository;
import com.example.multitenant.infrastructure.persistence.TenantRepository;
import com.example.multitenant.security.UserPrincipal;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UiController {
  private final ProjectService projectService;
  private final TenantSettingsService tenantSettingsService;
  private final TenantMembershipRepository membershipRepository;
  private final TenantRepository tenantRepository;

  public UiController(ProjectService projectService,
                      TenantSettingsService tenantSettingsService,
                      TenantMembershipRepository membershipRepository,
                      TenantRepository tenantRepository) {
    this.projectService = projectService;
    this.tenantSettingsService = tenantSettingsService;
    this.membershipRepository = membershipRepository;
    this.tenantRepository = tenantRepository;
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/ui/projects")
  public String projects(Model model, java.security.Principal principal) {
    List<Project> projects = projectService.list();
    model.addAttribute("projects", projects);
    model.addAttribute("settings", tenantSettingsService.getCurrentTenantSettings());
    model.addAttribute("tenants", tenantLinks(principal));
    return "projects";
  }

  @PostMapping("/ui/projects")
  public String createProject(@RequestParam("name") @NotBlank String name,
                              @RequestParam(value = "description", required = false) String description) {
    projectService.create(name, description);
    return "redirect:/ui/projects";
  }

  @GetMapping("/ui/tenants")
  public String tenants(Model model, java.security.Principal principal) {
    model.addAttribute("tenants", tenantLinks(principal));
    return "tenants";
  }

  private List<TenantLink> tenantLinks(java.security.Principal principal) {
    if (principal instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken token
        && token.getPrincipal() instanceof UserPrincipal userPrincipal) {
      UUID userId = userPrincipal.getUserId();
      return membershipRepository.findByUserId(userId).stream()
          .map(membership -> tenantRepository.findById(membership.getTenantId()).orElse(null))
          .filter(tenant -> tenant != null)
          .map(tenant -> new TenantLink(tenant.getName(), tenant.getKey()))
          .toList();
    }
    return List.of();
  }

  public record TenantLink(String name, String key) {}
}
