# Security

## Threats & Mitigations

### Tenant spoofing
- Enforce tenant context via header/subdomain only.
- JWT tenant claim must match resolved tenant.

### IDOR (Insecure Direct Object Reference)
- All repositories require `tenant_id` filters.
- Guard throws if cross-tenant entities are loaded.

### Cache leakage
- Cache keys include tenantId explicitly (`tenantSettings` cache).

### Secrets storage
- API keys are stored hashed (BCrypt).
- User passwords are stored hashed (BCrypt).

### Secure defaults
- Security headers enabled (X-Content-Type-Options, XSS Protection, Frame Options).
- Login endpoint rate-limited.

## RLS Hardening Pack (Optional)

Postgres RLS can add defense-in-depth:

```sql
ALTER TABLE projects ENABLE ROW LEVEL SECURITY;
CREATE POLICY tenant_isolation ON projects
  USING (tenant_id = current_setting('app.tenant_id')::uuid);
```

Implement carefully with integration tests. Misconfigurations can lead to outages.
