# Tenant Isolation

## Implemented Strategy: Shared Database, Shared Schema

All tenant-owned tables include a `tenant_id` column. Access is enforced by:

- Mandatory tenant resolution (header/subdomain)
- Repository queries that always filter by `tenant_id`
- `TenantEntityListener` guard that throws if a cross-tenant entity is loaded

This pattern is the most common multi-tenant baseline for early and mid-stage SaaS.

## Other Strategies (Documented Extension Points)

### Schema-per-tenant
- Stronger isolation, easier data export per tenant
- Higher operational overhead, migration complexity
- Not fully implemented: see `docs/LIMITATIONS.md`

### Database-per-tenant
- Best isolation and blast-radius control
- Expensive to operate at scale
- Not implemented: requires routing, provisioning, and monitoring automation

### Postgres Row-Level Security (RLS)
- Defense-in-depth in the database
- Requires precise policies and careful testing
- Provided as a hardening pack in `docs/SECURITY.md`

## Guard Pattern

We add a fail-safe guard that throws if a cross-tenant entity is loaded, even if a query accidentally forgets to filter.
This is validated in tests.
