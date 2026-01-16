# Limitations & Extension Points

This repo implements a strong baseline for shared-schema multi-tenancy. Enterprise-grade isolation options are documented but not fully implemented because mistakes are high-risk.

## Not Implemented (By Design)

- **Schema-per-tenant**: requires schema routing and migration orchestration.
- **Database-per-tenant**: needs automated provisioning, connection routing, and cost controls.
- **Postgres RLS hardening**: policies are tricky to get right; examples only.
- **SSO (SAML/OIDC) federation**: stub only; needs careful tenant discovery and IdP config.
- **Multi-region routing**: requires tenant placement, geo-routing, and failover.
- **Distributed cache isolation**: must isolate cache keys and eviction strategies.
- **Automated tenant lifecycle management**: provisioning, suspension, and deletion flows.

Each of these areas can introduce data leakage or outages if implemented incorrectly.
