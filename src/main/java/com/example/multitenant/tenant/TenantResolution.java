package com.example.multitenant.tenant;

import java.util.UUID;

public record TenantResolution(UUID tenantId, String tenantKey, TenantResolutionMethod method) {}
