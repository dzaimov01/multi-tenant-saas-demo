package com.example.multitenant.api;

import com.example.multitenant.application.ProjectService;
import com.example.multitenant.domain.Project;
import com.example.multitenant.security.TenantAuthorizationService;
import com.example.multitenant.domain.TenantRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
  private final ProjectService projectService;
  private final TenantAuthorizationService authorizationService;

  public ProjectController(ProjectService projectService, TenantAuthorizationService authorizationService) {
    this.projectService = projectService;
    this.authorizationService = authorizationService;
  }

  @GetMapping
  public List<ProjectResponse> list() {
    return projectService.list().stream().map(ProjectResponse::from).toList();
  }

  @PostMapping
  public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN, TenantRole.MEMBER);
    Project project = projectService.create(request.name(), request.description());
    return ProjectResponse.from(project);
  }

  @PutMapping("/{projectId}")
  public ProjectResponse update(@PathVariable UUID projectId, @Valid @RequestBody ProjectRequest request) {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN, TenantRole.MEMBER);
    Project project = projectService.update(projectId, request.name(), request.description());
    return ProjectResponse.from(project);
  }

  @DeleteMapping("/{projectId}")
  public void delete(@PathVariable UUID projectId) {
    authorizationService.requireRole(TenantRole.OWNER, TenantRole.ADMIN);
    projectService.delete(projectId);
  }

  public record ProjectRequest(@NotBlank String name, String description) {}

  public record ProjectResponse(UUID id, String name, String description) {
    public static ProjectResponse from(Project project) {
      return new ProjectResponse(project.getId(), project.getName(), project.getDescription());
    }
  }
}
