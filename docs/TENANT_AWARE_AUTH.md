# Tenant-aware Authentication & Authorization

## Login

- `POST /api/auth/login` authenticates a user and issues a JWT.
- If the user belongs to multiple tenants, a `tenantId` is required in the login request.
- JWT includes `tenantId` and `role` claims.

## Tenant Context

Tenant context is resolved using:

1. `X-Tenant-Id` header (UUID)
2. subdomain (`acme.localhost`)

Requests that provide a tenant context that does not match the JWT claim are rejected to prevent spoofing.

## Roles (RBAC)

Per-tenant roles:

- `OWNER`
- `ADMIN`
- `MEMBER`
- `VIEWER`

API endpoints use `TenantAuthorizationService` to require roles and enforce authorization.
