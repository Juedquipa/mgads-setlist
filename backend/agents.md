# Agents for setlist-backend

This file documents recommended agent usage and guidelines for the setlist-backend repository.

## Purpose

- Provide guidance for any automated agents (Copilot-style, CI bots, local assistants) that will interact with this repository.

## Recommended Agents

- Human-like coding assistant (local/IDE): Helpful for editing, refactors, and writing tests. Keep suggestions minimal and focused.
- CI automation agent: Runs tests, linting, and security checks on PRs. Should not merge PRs without human approval.

## Agent Guidelines

- Commit changes only after a human reviews the diff. Agents may create branches or open PRs but require human approval to merge.
- Avoid making destructive changes (mass deletions, credential changes) without explicit human confirmation.
- When proposing code changes, include a short rationale and link to any relevant tests or issues.

## Prompt / Instruction Templates

- Short edit request: "Update [file path] to fix X: change Y to Z and add a unit test covering the change. Explain the reason in the PR description."
- Refactor request: "Refactor the function `functionName` in [file path] for readability; keep behavior identical and add tests demonstrating equivalence."

## Local developer notes

- The project is a Python backend. Use the environment in `requirements.txt` for dependency hints.
- When running agents that execute code, run in an isolated virtual environment and run the test suite locally before opening PRs.

## Maintainers / Contact

- If an agent produces unexpected changes, contact the repository maintainers (project owner) for review and rollback.

## Example workflow

1. Agent opens a branch `agent/fix-xyz` with changes and a descriptive PR.
2. Human reviewer runs tests, verifies behavior, and merges after approval.

---
Created by repository automation. Update this file if agent policies change.
