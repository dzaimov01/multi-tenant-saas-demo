# Migrations

This project uses Flyway with baseline migrations for the shared schema.

## Tenant-safe Migration Guide

When changing tenant-owned tables:

1. Add the new column nullable or with a default.
2. Backfill in small batches if needed.
3. Deploy application code that reads the new column.
4. Enforce NOT NULL or stricter constraints after verifying backfill.

## Example Migrations

- `V1__init.sql`: baseline schema
- `V2__add_project_archived.sql`: adds `projects.archived` with a safe default

## Zero-downtime Considerations

- Avoid destructive column drops without dual-write support.
- For large tables, backfill outside the request path.
- Prefer additive schema changes first, then cleanup in later releases.
