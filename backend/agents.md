# Agents for setlist-backend (Neon Beats SETLIST)

This file documents recommended agent usage and repository-specific guidelines. It now includes a concise summary of the project's technical specifications (from the project specification document) so agents and humans have a common context when proposing changes.

## Purpose

- Provide guidance for any automated agents (Copilot-style, CI bots, local assistants) that will interact with this repository and the related mobile project.

## Project Context & Goals

**Problem Statement:** In bars and entertainment venues, the customer's music experience is passive. Requesting songs currently involves physically going to the DJ, which causes friction, lost requests, lack of filtering, and doesn't incentivize consumption.

**The Solution (Neon Beats SETLIST):** An Android Android application that digitizes and democratizes the music experience. Customers can request songs directly from their tables based on a credits system linked to their consumption, fully controlled by the DJ and the waitstaff.

**Key Objectives & Features:**

- **No-Friction Access:** Clients scan a table QR code to join without creating an account.
- **Credit System:** Waitstaff generates temporary 6-digit PINs tied to drink/food orders. Clients enter the PIN to earn "song request credits".
- **Spotify Integration:** Search and retrieve song metadata (title, artist, album, 30s preview) leveraging the public Spotify Web API.
- **Real-time Queue & Control:** A live, synced playback queue. The DJ can reorder, clear, or play/pause the queue.
- **Quality Control:** DJs pre-approve a internal "Bar Catalog". If clients propose songs outside the catalog, the DJ must review and approve/reject them before they enter the queue.

**System Roles:**

1. **Customer:** Scans the table QR, enters waitstaff PIN to unlock credits, searches for songs, requests tracks, and views the live queue.
2. **Waiter (Waitstaff):** Authenticated staff. Generates 6-digit PINs linked to tables to grant song request credits.
3. **Admin / DJ:** Authenticated owner/DJ. Manages the live queue, approves/rejects external song suggestions, oversees waitstaff, and configures bar settings.

## Tech Stack (high level)

- Mobile: Android (Android Studio, Jetpack Compose).
- Backend: Python (Django) + Django REST Framework, PostgreSQL. This repository uses Django (see [backend/requirements.txt](backend/requirements.txt) and `manage.py`)
- External services: Spotify Web API (Client Credentials flow), Firebase App Distribution for APK distribution, Docker for containerization, Railway/Render for deployment.

## Recommended Agents

- Local coding assistant: for focused edits, refactors, and tests (keep suggestions minimal and include rationale).
- CI automation agent: runs linting, formatting, and tests on PRs. Must not merge PRs without human approval.

## Agent Guidelines (repository-specific)

- **Language Policy:** All code, variables, models, endpoints, and documentation references must be strictly in English. **Do not use Spanish terms in the codebase.**
- Detect project type before modifying: check for `requirements.txt`, `manage.py`, `pyproject.toml`, and `mobile/` sources. This repository uses Python/Django — do not assume Node.js.
- Create branches for non-trivial changes (naming convention: `agent/<short-desc>`). Open a PR and include a clear description referencing relevant tests and the spec.
- Run or suggest running the appropriate test suites and linters for the target component (e.g., `npm test` / `yarn test`, `pytest` or Django test runner, Android instrumented/unit tests). Include exact commands in the PR description.
- Do not modify or commit secrets, credentials, or production configuration files. If a change requires secrets, create a PR and document required secrets for a human to apply.
- For changes affecting mobile UI/UX or APIs, include a concise compatibility note describing migration steps, database migrations, and client version impact.

## Prompt / Instruction Templates

- Short edit request: "Update [file path] to fix X: change Y to Z and add a unit test covering the change. Explain the reason in the PR description and list commands to run locally."
- Refactor request: "Refactor the function `functionName` in [file path] for readability; keep behavior identical and add tests demonstrating equivalence. Include commands to run tests and linters."

## Local developer notes

- Check manifests: [backend/requirements.txt](backend/requirements.txt) (Python), `package.json` (Node), and the `mobile/` folder for Android sources before making changes.
- When running agents that execute code, use isolated Python virtual environments and Android emulators as needed. Provide runnable commands in PRs:

- Example commands (Windows PowerShell):

```powershell
- python -m venv .venv
- .venv\Scripts\activate
- pip install -r backend/requirements.txt
- cd backend
- python manage.py migrate
- python manage.py runserver
- python manage.py test
```

- Docker / Postgres dev hint: prefer a `docker-compose.yml` with a Postgres service for local development; set `DATABASE_URL` or `.env` values via environment variables or CI secrets. Do not commit `.env` or secrets to the repo.

- Run linters and formatters before opening PRs (examples): `black .`, `isort .`, `flake8`, `pytest` or `python manage.py test`, `./gradlew lint`.

## Example workflow

1. Agent creates branch `agent/fix-xyz` and opens a PR with description, tests, and commands.
2. CI runs linters and tests. Human reviewer verifies behavior and merges after approval.
