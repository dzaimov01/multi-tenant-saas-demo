package com.example.multitenant.api;

import com.example.multitenant.application.AuthResult;
import com.example.multitenant.application.AuthService;
import com.example.multitenant.security.LoginRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private final LoginRateLimiter loginRateLimiter;

  public AuthController(AuthService authService, LoginRateLimiter loginRateLimiter) {
    this.authService = authService;
    this.loginRateLimiter = loginRateLimiter;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResult> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
    String key = httpRequest.getRemoteAddr();
    if (!loginRateLimiter.tryConsume(key)) {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
    AuthResult result = authService.login(request.email(), request.password(), request.tenantId());
    return ResponseEntity.ok(result);
  }

  public record LoginRequest(@Email @NotBlank String email,
                             @NotBlank String password,
                             UUID tenantId) {}
}
