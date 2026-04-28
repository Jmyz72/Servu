# Servu Architecture

Servu starts as a modular monorepo:

```text
React frontend -> Spring Boot API -> PostgreSQL
```

The frontend owns customer, vendor, and admin routes in one app for now. The backend owns all business logic, database access, and future payment integrations.

## Current Backend Modules

- `health`: basic service health endpoint.

## Planned Backend Modules

- `auth`: users, roles, and access control.
- `vendor`: vendor and branch management.
- `menu`: menus, categories, dishes, variants, and add-ons.
- `qrcode`: QR codes mapped to vendors, branches, and tables.
- `order`: order creation and lifecycle management.
- `payment`: payment records, providers, and webhooks.

## Key Direction

- Keep React separate from database and payment providers.
- Keep Spring Boot as a modular monolith until the product needs service boundaries.
- Use Flyway for database migrations.
- Use DTOs for API requests and responses instead of exposing persistence entities.
