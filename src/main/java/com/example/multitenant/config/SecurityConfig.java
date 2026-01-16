package com.example.multitenant.config;

import com.example.multitenant.application.TenantMetricsFilter;
import com.example.multitenant.security.JwtAuthenticationFilter;
import com.example.multitenant.tenant.TenantContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
                                                    JwtAuthenticationFilter jwtAuthenticationFilter,
                                                    TenantContextFilter tenantContextFilter,
                                                    TenantMetricsFilter tenantMetricsFilter) throws Exception {
    http
        .securityMatcher("/api/**")
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers("/api/health").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(jwtAuthenticationFilter, TenantContextFilter.class)
        .addFilterAfter(tenantMetricsFilter, JwtAuthenticationFilter.class)
        .headers(headers -> headers
            .contentTypeOptions(options -> {})
            .xssProtection(xss -> {})
            .frameOptions(frame -> frame.deny())
        );

    return http.build();
  }

  @Bean
  public SecurityFilterChain webSecurityFilterChain(HttpSecurity http,
                                                    TenantContextFilter tenantContextFilter,
                                                    TenantMetricsFilter tenantMetricsFilter) throws Exception {
    http
        .securityMatcher("/ui/**", "/", "/login", "/css/**")
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/ui/projects", true)
        )
        .addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(tenantMetricsFilter, TenantContextFilter.class)
        .headers(headers -> headers
            .contentTypeOptions(options -> {})
            .xssProtection(xss -> {})
            .frameOptions(frame -> frame.deny())
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }
}
