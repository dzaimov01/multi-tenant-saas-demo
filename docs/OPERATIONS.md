# Operations

## Logs

Structured JSON logs include:

- `correlationId`
- `tenantId`
- request metadata

Use these fields for per-tenant monitoring and incident response.

## Metrics

The demo exposes a basic metrics view at `/api/admin/metrics`:

- requests per tenant
- active users per tenant

## Health

- `/api/health`
- `/actuator/health`

## OpenTelemetry (Optional)

A production system should emit traces with tenant-aware attributes.
Use OpenTelemetry Java agent or SDK integration and attach `tenantId` and `correlationId` to spans.
