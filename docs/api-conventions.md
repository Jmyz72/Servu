# API and Module Conventions

These conventions guide Phase 1 foundation work and should be followed by later feature phases unless a phase documents a deliberate exception.

## Backend Modules

The backend remains a Spring Boot modular monolith. Feature code should live under the module package that owns the business capability:

- `auth`: authentication, users, roles, and access control.
- `vendor`: vendor tenants, branches, tables, staff assignments, and onboarding.
- `menu`: menus, categories, items, variants, add-ons, and availability.
- `qrcode`: QR code references mapped to vendor tenants, branches, and tables.
- `order`: customer order creation, order lifecycle, and vendor operations.
- `payment`: manual payment tracking, future provider records, and webhooks.

Shared code should only be introduced when at least two modules need the same behavior.

## API Shape

- Prefix backend HTTP APIs with `/api`.
- Use request and response DTOs at controller boundaries.
- Do not expose persistence entities directly through API responses.
- Keep controller classes thin; place business decisions in service classes.
- Validate request DTOs with Jakarta Bean Validation where practical.
- Return customer-safe DTOs for QR menu and order flows.

## Authentication Direction

- Use backend-managed authentication for platform admin, vendor owner/admin, and vendor staff users.
- Use role-based access with these MVP roles: `PLATFORM_ADMIN`, `VENDOR_ADMIN`, and `VENDOR_STAFF`.
- Keep customer QR ordering login-free for the MVP; QR access is scoped by resolving an active QR code to a vendor, branch, and table.
- Defer third-party OAuth, social login, and customer accounts until a later roadmap phase requires them.

## Error Responses

API errors should use a consistent JSON shape:

```json
{
  "timestamp": "2026-04-28T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/example",
  "fieldErrors": [
    {
      "field": "name",
      "message": "must not be blank"
    }
  ]
}
```

- Use `fieldErrors` for request validation failures.
- Return an empty `fieldErrors` array when the error is not field-specific.
- Do not return stack traces or persistence details to API clients.

## Persistence and Migrations

- Manage PostgreSQL schema changes with Flyway migrations in `backend/src/main/resources/db/migration`.
- Name migrations with Flyway's versioned format, for example `V2__create_vendor_tables.sql`.
- Keep migrations focused on one product capability when practical.
- Use tenant-scoped foreign keys for vendor-owned data once tenant tables are introduced.

## Tenancy and Access

- Treat the vendor as the tenant boundary.
- Resolve branch, table, QR code, menu, order, and payment records through their vendor tenant.
- Enforce tenant access in backend authorization and queries, not only in frontend filters.
- Customer QR access should resolve only the data needed for menu browsing and order placement.

## Frontend Routes

The React app owns customer, vendor, admin, and login surfaces:

- `/customer/:qrCodeId/menu`: customer QR menu and ordering flow.
- `/vendor/orders`: vendor order operations.
- `/admin`: platform administration.
- `/login`: authenticated staff and admin access.

Frontend code should call backend APIs through the shared API client configuration based on `VITE_API_BASE_URL`.

## Frontend States

- Customer QR screens should show clear states for loading the QR menu, invalid/inactive QR codes, empty menus, failed order submission, and successful order submission.
- Vendor screens should show loading states, empty order/menu states, and API errors without losing the current tenant context.
- Admin screens should show loading, empty vendor lists, and setup errors with enough context for platform operations.
- Login should distinguish invalid credentials from unavailable backend/network failures.
- Reusable UI components should be introduced only after the same state pattern appears in multiple screens.
