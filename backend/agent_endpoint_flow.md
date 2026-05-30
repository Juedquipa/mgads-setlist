# Endpoint Flow

This document describes the main backend API flow exposed by the project and how the endpoints relate to each other.

## High-level flow

1. A user authenticates with the JWT login endpoint.
2. Staff users manage tenants, tables, waiters, PIN codes, and queue moderation through authenticated endpoints.
3. A client scans a table QR code to create a temporary session.
4. The client uses that session token to validate PIN codes and request songs.
5. Queue and approval endpoints reflect the live song request state for the tenant.
6. Spotify endpoints are used to search tracks and fetch track details for the tenant's configured Spotify credentials.

## Auth flow

### `POST /api/auth/login/`

- Accepts `username` plus either `pin` or `password`.
- Returns a refresh token and an access token.
- If `pin` is provided, the backend authenticates against the user's persistent 4-digit staff PIN for that username.
- If `password` is provided, the backend uses the standard Django password authentication flow for that username.
- The custom token payload includes user role and tenant information.

### `POST /api/staff/login/`

- Staff login via username plus a persistent 4-digit PIN or via username and password.
- Accepts `username` plus either `pin` or `password` and returns refresh and access tokens.
- If `pin` is provided, the backend authenticates against the user's persistent staff PIN for that username.
- If `password` is provided, the backend uses the standard Django password authentication flow for that username.
- The access token includes the staff user's role and tenant information.
- This is separate from the client credit PIN flow used to unlock song request credits.

### `POST /api/auth/refresh/`

- Accepts a refresh token.
- Returns a new access token.

### Usage

- Authenticated staff endpoints use the JWT access token.
- Client endpoints do not use JWT; they use the temporary client session token described below.

## Client session flow

### `POST /api/client/session/`

- Input: `qr_code`.
- The endpoint validates that the QR code belongs to an active table.
- If valid, it creates a session tied to that table and tenant.
- Output includes the session token and current balance.

### Session token usage

- The client must send `X-Session-Token` on session-protected endpoints.
- The permission layer resolves the token to the active client session and attaches it to the request.

### `POST /api/client/pin-validate/`

- Requires `X-Session-Token`.
- Input: `code`.
- Validates that the PIN belongs to the same tenant and has not been used.
- Marks the PIN as used.
- Adds the PIN credits to the current session balance.

### `POST /api/client/request-song/`

- Requires `X-Session-Token`.
- Input: Spotify track data such as `spotify_id`, `title`, and `artist`.
- Checks that the session has enough credits.
- Creates or reuses the track record.
- Creates a pending song request for the tenant.
- Decrements the session balance by one credit.
- Broadcasts a queue update to websocket listeners.

### `GET /api/client/requests/`

- Requires `X-Session-Token`.
- Returns all requests created by the current client session.
- Each item includes the request status so the client can see whether it is pending, playing, played, or skipped.
- The list is ordered with the newest requests first.

## Queue and approvals flow

### `GET /api/queue/`

- Returns the pending song requests for the authenticated user's tenant.
- Ordered by request time.
- Intended for staff or moderators who need to see the active queue.

### `DELETE /api/queue/clear/`

- Restricted to users with the ADMIN role.
- Removes all pending requests for the tenant.
- Broadcasts a queue update after the delete.

### `GET /api/approvals/pending/`

- Returns pending requests that can be approved or rejected.
- This is the moderation queue view.

### `PUT /api/approvals/{id}/approve/`

- Fetches a pending request for the authenticated user's tenant.
- Marks it as playing.
- Broadcasts a queue update.
- Returns the updated request.

### `PUT /api/approvals/{id}/reject/`

- Fetches a pending request for the authenticated user's tenant.
- Marks it as skipped.
- Broadcasts a queue update.
- Returns the updated request.

## Tables flow

### `GET /api/tables/`

- Lists the tables owned by the authenticated user's tenant.

### `POST /api/tables/`

- Creates a table for the authenticated user's tenant.
- The table is automatically attached to the tenant.

### `GET /api/tables/{id}/session/`

- Returns the active client session for a given table if one exists.
- Returns `404` if the table does not currently have an active session.

## Waiters flow

### `GET /api/waiters/`

- Lists waiter users for the authenticated user's tenant.

### `POST /api/waiters/`

- Creates a waiter in the current tenant.
- The role is forced to WAITER.

### `GET`, `PUT`, `PATCH`, `DELETE /api/waiters/{id}/`

- Standard tenant-scoped CRUD operations for waiter users.
- The queryset is always limited to the authenticated user's tenant.

## PIN code flow

### `GET /api/pin-codes/`

- Lists PIN codes for the tenant.

### `POST /api/pin-codes/`

- Creates a new PIN code.
- The code value is generated automatically.

### `GET`, `PUT`, `PATCH`, `DELETE /api/pin-codes/{id}/`

- Standard tenant-scoped CRUD operations.

### Staff PIN storage

- Staff users with the `ADMIN` or `WAITER` role have a persistent 4-digit `staff_pin` stored on the user record.
- The PIN is auto-generated when the staff user is created, and existing staff users are backfilled through a migration.
- The staff PIN is visible in the Django admin to help operators recover or confirm login credentials.
- This staff PIN is only for staff authentication and must not be confused with client credit PINs.

## Spotify flow

### `GET /api/spotify/search/?q=...`

- Uses the tenant's Spotify credentials.
- Exchanges tenant credentials for a Spotify access token.
- Searches Spotify tracks using the query string.
- Returns the Spotify search envelope with track items.

### `GET /api/spotify/track/{track_id}/`

- Uses the tenant's Spotify credentials.
- Fetches a single Spotify track by ID.
- Returns the Spotify track object.

## Supporting behavior

### Queue broadcasts

- When a song request is created, approved, rejected, or the queue is cleared, the backend emits a websocket message to the tenant queue group.
- This keeps live queue clients synchronized.

### Tenant scoping

- Most authenticated staff endpoints are tenant-scoped.
- Users only see and mutate data inside their own tenant.
- Client session endpoints are also tenant-aware through the table and PIN ownership checks.

## Doc intent

This flow document reflects the current runtime behavior of the backend API, not only the URL structure.
If the implementation changes, this file should be updated alongside the schema annotations so the docs and the code stay aligned.
