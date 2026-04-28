# Servu Architecture

Servu starts as a modular monorepo:

```text
React frontend -> Spring Boot API -> PostgreSQL
```

The frontend owns customer, vendor, and admin routes in one app for now. The backend owns all business logic, database access, and future payment integrations.

## Current Backend Modules

- `health`: basic service health endpoint.
- `auth`: package boundary for users, roles, and access control.
- `vendor`: package boundary for vendor tenants, branches, tables, and onboarding.
- `menu`: package boundary for menus, categories, dishes, variants, and add-ons.
- `qrcode`: package boundary for QR codes mapped to vendors, branches, and tables.
- `order`: package boundary for order creation and lifecycle management.
- `payment`: package boundary for manual payment tracking and future payment integrations.

## Planned Feature Work

- Add implementation classes inside the package boundaries as each roadmap phase is built.
- Introduce vendor-level tenant tables before adding menu, QR, order, or payment records.
- Keep online payment providers behind backend APIs and add them after the manual payment MVP.

## Key Direction

- Keep React separate from database and payment providers.
- Keep Spring Boot as a modular monolith until the product needs service boundaries.
- Use Flyway for database migrations.
- Use DTOs for API requests and responses instead of exposing persistence entities.
- Use backend-managed staff/admin authentication for the MVP, with customer QR ordering kept login-free.
- Use a shared API error response shape for validation and runtime failures.
- Follow the conventions in `docs/api-conventions.md`.
