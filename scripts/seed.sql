-- Demo tenants
INSERT INTO tenants (id, tenant_key, name, theme_color, created_at)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'acme', 'Acme Labs', '#D76B4A', now()),
  ('22222222-2222-2222-2222-222222222222', 'globex', 'Globex Corp', '#3A6EA5', now());

INSERT INTO tenant_settings (tenant_id, branding_name, theme_color, feature_flags, rate_limit_per_min, updated_at)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'Acme Labs', '#D76B4A', '{"beta": true}', 120, now()),
  ('22222222-2222-2222-2222-222222222222', 'Globex Corp', '#3A6EA5', '{"beta": false}', 80, now());

-- Demo users (password: Password123!)
INSERT INTO users (id, email, password_hash, display_name, created_at)
VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'owner@acme.com', '$2y$10$fUC4hGy8k6NKdToW2F0gDOHgRGFHtPj18RPcE9hry7/GJCTKk4yg6', 'Ava Owner', now()),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'user@acme.com', '$2y$10$fUC4hGy8k6NKdToW2F0gDOHgRGFHtPj18RPcE9hry7/GJCTKk4yg6', 'Mia Member', now()),
  ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'owner@globex.com', '$2y$10$fUC4hGy8k6NKdToW2F0gDOHgRGFHtPj18RPcE9hry7/GJCTKk4yg6', 'Gus Owner', now());

INSERT INTO tenant_memberships (id, tenant_id, user_id, role, created_at)
VALUES
  ('d1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'OWNER', now()),
  ('d2222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'MEMBER', now()),
  ('d3333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'OWNER', now());

INSERT INTO projects (id, tenant_id, name, description, created_at)
VALUES
  ('e1111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'Onboarding Redesign', 'Streamline signup flow', now()),
  ('e2222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'Supply Chain Revamp', 'Improve vendor tracking', now());
