# Multi-tenant SaaS Demo

A production-grade multi-tenant blueprint for real SaaS products.

## What problems it solves

- Tenant isolation with shared schema and strict guards
- Tenant-aware authentication + RBAC
- Safe migrations and tenant settings
- Audit logging, metrics, and operational visibility

## Architecture

```mermaid
graph TD
  Client -->|Header/Subdomain| API
  API --> TenantFilter
  TenantFilter --> Auth
  Auth --> Services
  Services --> DB[(Postgres)]
  Services --> Cache[(Tenant Settings Cache)]
```

## Quickstart

```bash
cp .env.example .env

docker-compose up --build
```

Open:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Seed data

After the app is up and migrations are applied:

```bash
psql postgresql://saas:saas@localhost:5432/saas_demo -f scripts/seed.sql
```

## Demo flow

### Login (header-based tenant)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"owner@acme.com","password":"Password123!","tenantId":"11111111-1111-1111-1111-111111111111"}'
```

Use the returned JWT:

```bash
curl http://localhost:8080/api/projects \
  -H 'Authorization: Bearer <JWT>' \
  -H 'X-Tenant-Id: 11111111-1111-1111-1111-111111111111'
```

### UI (subdomain-based tenant)

Open `http://acme.localhost:8080/login` and sign in with:

- `owner@acme.com` / `Password123!`
- `owner@globex.com` / `Password123!`

Switch tenants via links at the top of the projects page.

## Tenant boundary guarantees

- Every tenant-owned table includes `tenant_id`
- All tenant-owned queries are filtered by `tenant_id`
- A guard throws if any cross-tenant entity is loaded
- JWT tenant claim must match resolved tenant

## Extension points (advanced enterprise modules)

- Schema-per-tenant / DB-per-tenant routing
- Postgres RLS hardening pack
- SSO (SAML/OIDC) federation
- Multi-region tenant routing
- Distributed cache isolation
- Automated tenant provisioning lifecycle

See `docs/LIMITATIONS.md` for why these are risky if implemented incorrectly.

## Suggested commit plan (example)

1. `chore: bootstrap spring boot pom and config`
2. `feat: add tenant context and resolution`
3. `feat: add domain entities and repositories`
4. `feat: add jwt auth and security config`
5. `feat: add project service and api`
6. `feat: add admin tenant management`
7. `feat: add audit logging and metrics`
8. `feat: add api keys support`
9. `feat: add thymeleaf ui`
10. `feat: add flyway migrations`
11. `test: add tenant resolution unit tests`
12. `test: add testcontainers isolation tests`
13. `docs: add architecture and security docs`
14. `docs: add README and operations guidance`

## Professional services

Need help with enterprise isolation, SSO federation, or multi-region routing? Reach out for architecture reviews and implementation support.

---

Explore the docs in `/docs` for deeper guidance.
