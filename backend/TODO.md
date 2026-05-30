# Neon Beats SETLIST - Backend TODO & Architecture Plan (Multi-Tenant)

This document outlines the detailed implementation phases, Django apps structure, database models, and REST API endpoints required to build the backend according to the technical specifications.

## Core Constraint: Multi-Tenancy

The system architecture revolves around **Tenants**. A `Tenant` represents a physical location (Bar, Nightclub, Restaurant).

- Admins and Meseros are attached to a specific Tenant.
- Clients can join any Tenant (by scanning that Tenant's specific Table QR codes).
- The playback queue, songs catalog, and staff are strictly isolated per Tenant.
- Each Tenant holds its **own Spotify credentials** (Client ID and Secret) to manage its unique catalog and playback proxy.
- Tenants are managed strictly through the default Django Admin panel by super-users.

## 1. Django Apps Structure

We will split the logic into focused, decoupled Django apps:

- **`tenants`**: Manages the `Tenant` core model (locations, Spotify credentials).
- **`users`**: Manages custom user models (Admin/DJ, Mesero), Tenant association, JWT authentication, and permissions.
- **`venues`**: Venue-specific entities tied to a Tenant: `Table`, `Catalog` (Bar's Pre-Approved Songs).
- **`music_queue`**: Handles the core transaction loops: `CodigoPIN`, `Sesion`, `Cancion`, `Solicitud` (Song Requests), and `Cola` (Live Queue).
- **`spotify`**: Acts as a proxy and caching layer for the Spotify Web API. It pulls credentials from the current context's `Tenant`.

---

## 2. Models Definition

*All models should inherit from a common base model with `created_at` and `updated_at` timestamps.*

### App: `tenants`

- **`Tenant`**
  - `name`: CharField (e.g., "Rock Bar Centro")
  - `type`: CharField (Choices: BAR, NIGHTCLUB, RESTAURANT)
  - `spotify_client_id`: CharField
  - `spotify_client_secret`: CharField
  - `is_active`: BooleanField (Default: True)

### App: `users`

- **`User`** (inherits `AbstractUser`)
  - `role`: CharField (Choices: ADMIN, WAITER)
  - `tenant`: ForeignKey to `tenants.Tenant` (Null for Superusers, required for Admin/Waiter)
  - *Standard Django Auth fields used for username, name, password_hash.*

### App: `venues`

- **`Table`**
  - `tenant`: ForeignKey to `tenants.Tenant`
  - `number`: IntegerField (or CharField for names like "Table 1")
  - `qr_code`: UUIDField/CharField (Unique identifier embedded in the QR)
  - `is_active`: BooleanField (Default: True)

- **`Catalog`**
  - `tenant`: ForeignKey to `tenants.Tenant`s
  - `track`: ForeignKey to `music_queue.Track`
  - `is_active`: BooleanField (Default: True)

### App: `music_queue`

- **`PinCode`**
  - `pin_code`: CharField (6 digits)
  - `table`: ForeignKey to `venues.Table`
  - `waiter`: ForeignKey to `users.User`
  - `credits`: IntegerField
  - `status`: CharField (Choices: ACTIVE, USED, EXPIRED)
  - `expires_at`: DateTimeField

- **`Session`**
  - `table`: ForeignKey to `venues.Table`
  - `available_credits`: IntegerField
  - `used_credits`: IntegerField
  - `started_at`: DateTimeField (auto_now_add)
- **`Track`**
  - `spotify_id`: CharField (Unique index globally to avoid duplicating metadata)
  - `title`: CharField
  - `artist`: CharField
  - `duration_ms`: IntegerField (milliseconds)
  - `thumbnail_url`: URLField
  - `source`: CharField (Choices: CATALOG, EXTERNAL)
- **`Request`**
  - `track`: ForeignKey to `Track`
  - `session`: ForeignKey to `Session`
  - `status`: CharField (Choices: PENDING, APPROVED, REJECTED, PLAYED)
  - `created_at`: DateTimeField (auto_now_add)
- **`PlaybackQueue`**
  - `tenant`: ForeignKey to `tenants.Tenant` (Denormalized to easily grab the entire queue for a bar, though it could be inferred via Request -> Session -> Table)
  - `request`: ForeignKey to `Request`
  - `position`: IntegerField (For reordering via Drag & Drop)
  - `is_active`: BooleanField

---

## 3. Endpoints (Django REST Framework)

*Tenancy Isolation: For Admin/Waiter endpoints, the `Tenant` is inferred implicitly via request.user.tenant. For Client endpoints, the `Tenant` is inferred via the JWT issued upon scanning a valid `qr_code` (which contains the `Table` and `Tenant`).*

### Auth (`/api/auth/`)

- `POST /login/`: Admin/Waiter login (Returns JWT).

- `POST /refresh/`: Refresh JWT.
- `POST /validate-qr/`: Validates `qr_code` and creates/returns a Client `Session` (likely issuing a custom JWT for the client).

### Tables & Shifts (`/api/tables/`)

- `GET /`: List all tables (Admin/Waiter).

- `POST /`: Admin creates a new table.
- `GET /<id>/session/`: Gets active session data for a table.

### PIN Codes (`/api/pin-codes/`)

- `POST /generate/`: Waiter generates a PIN.

- `POST /validate/`: Client inputs PIN to unlock credits.
- `GET /waiter/<id>/`: Waiter views their generated pins for the shift.

### Queue & Requests (`/api/queue/`)

- `GET /`: Returns the live, ordered queue.

- `POST /request/`: Client requests a song (deducts 1 credit).
- `PUT /reorder/`: Admin updates positions in bulk.
- `DELETE /<id>/`: Admin removes a specific song.
- `DELETE /`: Admin clears the queue.

### Approvals (`/api/approvals/`)

- `GET /pending/`: Admin views external suggestions pending review.

- `PUT /<id>/approve/`: Moves from pending to accepted in the `PlaybackQueue`.
- `PUT /<id>/reject/`: Marks as rejected (refunds credit possibly?).

### Spotify Proxy (`/api/spotify/`)

- `GET /search/?q=`: Search Spotify catalog via backend proxy.

- `GET /track/<id>/`: Details/Preview for a track.

### Admin/Manager (`/api/admin/`)

- `GET /waiters/`, `POST /waiters/`, `PUT /waiters/<id>/`, `DELETE /waiters/<id>/`: CRUD for waitstaff.

- `GET /statistics/tables/`, `GET /statistics/waiters/`: Usage metrics.

---

## 4. Pending Implementation Steps (Next Actions)

- [ ] Initialize missing Django apps (`tenants`, `users`, `venues`, `music_queue`, `spotify`).
- [ ] Install missing dependencies (e.g. `djangorestframework-simplejwt`, `requests` or `httpx` for Spotify calls).
- [ ] Implement multi-tenant middleware or Base ViewSets to automatically scope queries by `Tenant`.
- [ ] Implement abstract base models and the specific models mapped above, ensuring `Tenant` FKs are enforced properly.
- [ ] Connect custom User model to `settings.py` (`AUTH_USER_MODEL`).
- [ ] Configure Django admin (`admin.py`) for superusers to securely manage `Tenant` creation and inject Spotify credentials.
- [ ] Decide on real-time update strategy (polling vs Django Channels / WebSockets) for isolated queues.
