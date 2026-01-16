package com.example.multitenant.application;

import java.util.UUID;

public record AuthResult(String token, UUID userId, String displayName, UUID tenantId, String role) {}
