# Servu Product Roadmap

Servu is a multi-tenant QR food ordering platform for restaurants, cafes, and food vendors. The first releasable product should let a customer scan a table QR code, browse a vendor menu, place an order, and let vendor staff manage that order without requiring online payment integration.

This roadmap combines product requirements and engineering phases. It is intentionally high level enough to guide implementation without locking exact API paths, database columns, or UI copy before each phase is designed in detail.

## Product Direction

### Target Users

- **Customer**: scans a QR code, browses the menu, configures items, submits an order, and receives order/payment status.
- **Vendor staff**: receives incoming orders, updates order status, coordinates kitchen/service workflow, and marks manual payments.
- **Vendor owner/admin**: manages branches, tables, staff access, menus, availability, and vendor-level reporting.
- **Platform admin**: onboards vendors, monitors platform health, manages tenant access, and reviews cross-vendor summaries.

### Tenant Model

Servu uses vendor-level tenancy:

```text
Vendor tenant -> Branches -> Tables -> QR codes -> Customer menu/order session
```

- A vendor tenant represents one restaurant brand, business, or food operator.
- A vendor can have one or more branches.
- Each branch can have many tables.
- Each table can have one or more QR code records, allowing rotation or reissue without changing the table identity.
- Customer QR access must resolve to a vendor, branch, and table before showing menu or accepting orders.

### Architecture Direction

- Keep the frontend as a React + TypeScript app with customer, vendor, admin, and login routes.
- Keep the backend as a Spring Boot modular monolith.
- Keep PostgreSQL as the system of record, managed through Flyway migrations.
- Keep business logic and integrations in the backend.
- Use DTOs for API requests and responses; do not expose persistence entities directly.
- Treat cash/manual payment tracking as the MVP payment flow.
- Treat online payments, provider webhooks, and settlement handling as a later hardening phase.

## Phase 1: Foundation

### Goal

Establish the technical foundation needed to build the product safely and consistently.

### In Scope

- Confirm project structure, local development workflow, and environment configuration.
- Add initial Flyway migration structure if missing.
- Define core domain boundaries for `auth`, `vendor`, `menu`, `qrcode`, `order`, and `payment`.
- Decide authentication direction for platform admin, vendor owner/admin, and vendor staff.
- Establish backend DTO, validation, error response, and controller conventions.
- Establish frontend route and API client conventions.

### Out of Scope

- Full vendor onboarding workflow.
- Full menu CRUD.
- Customer ordering.
- Payment provider integration.
- Production deployment automation.

### Backend Work

- Keep Spring Boot as the API and business logic layer.
- Add or confirm Flyway migration location and naming convention.
- Create package/module boundaries before adding feature logic.
- Define shared API response and validation patterns where needed.
- Keep persistence entities private to backend modules.

### Frontend Work

- Keep one React app with separate customer, vendor, admin, and login areas.
- Confirm route naming conventions for QR menu, vendor orders, admin dashboard, and login.
- Confirm API client configuration through `VITE_API_BASE_URL`.
- Establish reusable layout and loading/error state patterns.

### Acceptance Criteria

- A new developer can run frontend and backend using documented local commands.
- Backend tests run successfully.
- Frontend build and lint commands are documented and expected to pass.
- The next feature phase can add database migrations without changing project structure.
- Module boundaries are documented clearly enough to avoid mixing customer, vendor, and admin concerns.

### Key Risks or Decisions

- Authentication choices affect nearly every later phase.
- Weak module boundaries will make tenant isolation harder to enforce later.
- Database migration discipline should start before domain tables are added.

## Phase 2: Tenant & Vendor Management

### Goal

Allow the platform to represent vendors, branches, tables, QR codes, and vendor staff access.

### In Scope

- Vendor tenant records.
- Branch records under a vendor.
- Table records under a branch.
- QR code records mapped to vendor, branch, and table.
- Vendor staff and vendor owner/admin role assignments.
- Platform admin vendor onboarding workflow.

### Out of Scope

- Menu item management.
- Customer cart and order placement.
- Real-time order workflow.
- Online payments.
- Advanced billing or subscription plans.

### Backend Work

- Add tenant-aware vendor, branch, table, QR code, and staff models.
- Enforce that branch, table, QR code, menu, and order access stays within the resolved vendor tenant.
- Provide DTO-based APIs for platform admin onboarding and vendor management.
- Add validation for unique vendor identifiers and active/inactive QR codes.

### Frontend Work

- Add admin screens for vendor onboarding and vendor overview.
- Add vendor owner/admin screens for branch and table management.
- Add QR code management views for creating, viewing, disabling, and reissuing QR references.
- Show tenant context clearly in vendor/admin workflows.

### Acceptance Criteria

- A platform admin can create a vendor tenant.
- A vendor tenant can have at least one branch and table.
- A QR code reference resolves to exactly one active vendor, branch, and table.
- Vendor staff can only access records for their assigned vendor.
- Inactive QR codes cannot start a customer ordering session.

### Key Risks or Decisions

- Tenant isolation is a correctness and security requirement, not just a UI filter.
- QR code references should be opaque and rotatable.
- Staff role names and permissions should stay minimal until real workflows require more detail.

## Phase 3: Menu Management

### Goal

Allow vendor users to maintain menus that customers can browse from a QR code.

### In Scope

- Menu categories.
- Menu items or dishes.
- Item pricing, descriptions, and availability.
- Variants such as size or portion.
- Add-ons such as toppings, sides, or modifiers.
- Branch-level availability if a vendor operates multiple branches.

### Out of Scope

- Customer cart checkout.
- Kitchen routing.
- Discounts, vouchers, tax rules, or service charge automation.
- Inventory management.
- Image upload optimization beyond basic support if added.

### Backend Work

- Add tenant-aware menu models for categories, items, variants, add-ons, and availability.
- Expose DTO-based APIs for vendor menu management.
- Expose a customer-safe menu read model resolved by QR code.
- Validate prices, required selections, availability, and tenant ownership.

### Frontend Work

- Add vendor menu management screens.
- Add customer menu browsing screen using the QR route.
- Support visible unavailable states without allowing unavailable items to be ordered.
- Keep customer menu UI optimized for mobile scanning and ordering.

### Acceptance Criteria

- Vendor users can create, update, disable, and reorder categories and items.
- Customers can view the correct menu after scanning a valid QR code.
- Unavailable categories, items, variants, and add-ons are not orderable.
- Customer-facing menu data does not expose vendor-only fields.
- Menu APIs keep data scoped to the current vendor tenant.

### Key Risks or Decisions

- Variant and add-on rules can become complex; start with simple, validated option groups.
- Branch-specific menus should not duplicate vendor-wide data unless necessary.
- Menu data should be read-efficient because customer QR access depends on it.

## Phase 4: QR Customer Ordering MVP

### Goal

Let customers place table-linked orders from a QR code with manual/cash payment tracking.

### In Scope

- QR code session resolution.
- Customer cart.
- Item customization using variants and add-ons.
- Order submission linked to vendor, branch, table, and QR code.
- Order totals based on menu pricing at submission time.
- Manual/cash payment status such as unpaid, paid, refunded, or voided if needed.
- Customer order confirmation view.

### Out of Scope

- Online payment collection.
- Payment provider webhooks.
- Loyalty accounts.
- Customer login.
- Complex promotions or discounts.

### Backend Work

- Add order creation and order item models.
- Capture price snapshots at order time.
- Validate submitted items against active menu and availability.
- Prevent cross-tenant or cross-branch order creation.
- Track manual payment status separately from order preparation status.

### Frontend Work

- Add mobile-first cart and checkout flow to the customer menu route.
- Show selected table or branch context after QR resolution.
- Show order submission confirmation and current order status where available.
- Handle invalid, inactive, or expired QR code states clearly.

### Acceptance Criteria

- A customer can scan a valid QR code, build a cart, and submit an order.
- The order is linked to the correct vendor, branch, table, and QR code.
- Submitted totals use server-side pricing, not client-trusted totals.
- Invalid or inactive QR codes cannot create orders.
- MVP order payment status can be tracked manually by vendor staff.

### Key Risks or Decisions

- Server-side validation must be authoritative for menu availability and pricing.
- Customer sessions should avoid requiring accounts unless later requirements demand it.
- Manual payment status should not be mixed with kitchen/order fulfillment status.

## Phase 5: Vendor Order Operations

### Goal

Give vendor staff a reliable workflow for receiving, preparing, serving, and closing orders.

### In Scope

- Vendor order list.
- Order detail view.
- Order status updates.
- Manual payment status updates.
- Basic filtering by status, branch, and table.
- Clear customer notes and item customization display.

### Out of Scope

- Full kitchen display system.
- Printer integration.
- Driver/delivery workflow.
- Advanced staff productivity analytics.
- Real-time push if polling is sufficient for the first release.

### Backend Work

- Add order status transition rules.
- Add vendor-scoped order read APIs.
- Add APIs for updating fulfillment status and manual payment status.
- Keep order status history if needed for auditability.
- Ensure vendor staff cannot access orders outside their vendor tenant.

### Frontend Work

- Build vendor order list and detail screens.
- Show new, in-progress, ready, served/completed, and cancelled states.
- Provide clear status action controls for staff.
- Make item variants, add-ons, and customer notes easy to scan during service.

### Acceptance Criteria

- Vendor staff can see incoming orders for their vendor.
- Vendor staff can update valid order statuses.
- Invalid status transitions are rejected.
- Manual payment status can be updated without changing fulfillment status.
- Order screens remain usable during active service periods.

### Key Risks or Decisions

- Status transitions should be simple enough for staff to use under pressure.
- Real-time updates are valuable but should not block MVP if polling is acceptable.
- Auditability becomes more important once payment and refunds are introduced.

## Phase 6: Admin & Reporting

### Goal

Support platform operations and give vendors basic visibility into order and sales performance.

### In Scope

- Platform admin vendor overview.
- Vendor status controls such as active, suspended, or disabled.
- Basic vendor order summary.
- Basic branch/table order reporting.
- Basic sales summary based on manual payment status.
- Operational health checks for tenant setup completeness.

### Out of Scope

- Accounting-grade financial reporting.
- Tax filing reports.
- Subscription billing.
- Advanced dashboards or forecasting.
- Data warehouse or BI integration.

### Backend Work

- Add aggregate read models for vendor, branch, table, order, and payment summaries.
- Keep report queries tenant-scoped unless the caller is a platform admin.
- Add filters for date range, vendor, branch, order status, and payment status.
- Avoid mixing operational reports with payment provider settlement reports.

### Frontend Work

- Expand admin dashboard for platform-level vendor monitoring.
- Add vendor owner/admin reporting views.
- Show basic totals, counts, and status breakdowns.
- Provide empty states for new vendors with no orders.

### Acceptance Criteria

- Platform admins can review vendor status and setup health.
- Vendor owners can see order and sales summaries for their own tenant.
- Reports do not expose another vendor's data.
- Manual payment status is reflected in sales summaries.
- Date range filtering works consistently across summary views.

### Key Risks or Decisions

- Reporting should be useful without becoming an accounting system too early.
- Cross-tenant reporting must remain platform-admin only.
- Summary data may need optimization after real order volume is known.

## Phase 7: Online Payments & Hardening

### Goal

Add online payment support and production readiness after the manual payment ordering workflow is stable.

### In Scope

- Payment provider selection and integration.
- Payment intent or checkout session creation.
- Payment records linked to orders.
- Webhook handling and verification.
- Refund or void handling where provider support exists.
- Production security, observability, backup, and deployment hardening.

### Out of Scope

- Multiple payment providers at launch unless required.
- Complex marketplace settlement or split payments unless required by business model.
- Vendor subscription billing unless separately prioritized.
- Native mobile apps.

### Backend Work

- Add payment provider abstraction only if it reduces real integration complexity.
- Store provider references, payment status, and webhook events.
- Verify webhook signatures and process events idempotently.
- Reconcile order payment status from provider-confirmed events.
- Add production-grade logging, metrics, error handling, and backup expectations.

### Frontend Work

- Add customer online payment entry point after order review.
- Show pending, paid, failed, cancelled, and refunded states.
- Add vendor-visible payment status that distinguishes manual and online payments.
- Add admin/support views for payment troubleshooting where needed.

### Acceptance Criteria

- Customers can complete an online payment for an eligible order.
- Payment status updates are driven by verified backend/provider events.
- Duplicate webhooks do not duplicate payment effects.
- Vendor and admin users can distinguish manual and online payment records.
- Production readiness checklist is documented and reviewed before launch.

### Key Risks or Decisions

- Payment integration introduces security, compliance, and reconciliation requirements.
- Provider choice affects checkout UX, fees, payout model, and regional support.
- Online payment status must not rely only on frontend redirects.

## Cross-Phase Requirements

- Enforce vendor-level tenancy in backend authorization and queries.
- Keep customer QR access limited to customer-safe data.
- Keep DTOs separate from persistence entities.
- Add or update tests when behavior changes.
- Prefer small, focused migrations and reversible product decisions.
- Document assumptions when business rules are not finalized.

## Open Decisions

- Authentication provider and session strategy.
- Exact vendor staff role matrix.
- Whether branch-specific menus are overrides or independent menus.
- Whether QR codes should expire automatically or only be manually rotated.
- Whether vendor order updates require real-time push before MVP launch.
- Online payment provider and settlement model.
