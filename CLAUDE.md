# CLAUDE.md

## MANDATORY: load the `automated-tests` and `code-standards` skills before changing code

Whenever any code is created or changed, in any module, you MUST load both project skills **before touching the code**, with no exceptions (including small tweaks such as a one-line regex change):

- `automated-tests` (`.claude/skills/automated-tests/SKILL.md`): testing rules.
- `code-standards` (`.claude/skills/code-standards/SKILL.md`): coding rules (method size, parameters, early returns, constants, etc.).

Rules:

- Do not edit code or tests until both skills are loaded.
- Do not finish the task without checking that the behavior is covered by tests and that the code follows the standards.

## Project layout

| Module      | Stack                                     | Default port                                  |
| ----------- | ----------------------------------------- | --------------------------------------------- |
| `backend/`  | Java 17, Spring Boot 4 (Maven wrapper)    | `8080`                                        |
| `frontend/` | React 19, Vite, TypeScript                | `5173` (Vite default)                         |
| `e2e/`      | Playwright                                | — (expects the frontend on `5174`, see below) |
| Database    | PostgreSQL 16 (Docker), schema via Flyway | `5432`                                        |

## Backend architecture (hexagonal)

Package root: `gabriel.tiziano.trade_platform`.

| Package / class                                   | Role                                                                                                                                                                   |
| ------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `controller/` (`SignupController`)                | Input adapter: translates HTTP ↔ service. Validation errors become `422` via `@ExceptionHandler`. No business rules here.                                              |
| `service/` (`AccountService`)                     | Business rules (validations, id generation). Knows nothing about HTTP or the database. Inputs/outputs are records (`SignupInput`, `SignupOutput`, `GetAccountOutput`). |
| `repository/AccountRepository`                    | Port (interface) consumed by the service.                                                                                                                              |
| `repository/AccountRepositoryDatabase`            | Output adapter used in production (`@Repository`, `JdbcClient`, PostgreSQL).                                                                                           |
| `src/test/.../repository/AccountRepositoryMemory` | In-memory adapter used only by unit tests. It lives in `src/test` and has **no** `@Repository`, so Spring never sees two implementations.                              |
| `Validate*` classes                               | Pure validation rules (name, email, CPF, password).                                                                                                                    |

`ValidateCpf` is legacy code kept on purpose for the course's refactoring exercise. Do not refactor it unless explicitly asked.

## Install dependencies

```bash
cd frontend && npm install
cd e2e && npm install && npx playwright install chromium   # first time only: downloads the browser
cd backend && ./mvnw dependency:resolve                    # optional: Maven resolves on the first build anyway
```

## Run the projects

```bash
# Database (required by the backend and by every backend test that starts Spring)
docker compose -f backend/docker/docker-compose.yml up -d

# Backend (http://localhost:8080)
cd backend && ./mvnw spring-boot:run

# Frontend (http://localhost:5173)
cd frontend && npm run dev

# Frontend on the port the E2E tests expect (http://localhost:5174)
cd frontend && npm run dev -- --port 5174
```

Other frontend scripts: `npm run build`, `npm run lint`, `npm run preview`.

## Database (Docker + Flyway)

PostgreSQL runs on **port `5432`** (container `trade-postgres`; user `postgres`, password `123456`, database `app`). Data is kept in the named volume `trade-postgres-data`.

The schema is managed by **Flyway** migrations in `backend/src/main/resources/db/migration`, applied automatically when the application (or a Spring test) starts. Tables live in the `app` schema (e.g. `app.account`).

- To change the schema, create a **new** migration (`V2__description.sql`, `V3__...`), with two underscores after the version.
- Never edit a migration that has already been applied.

```bash
# Start
docker compose -f backend/docker/docker-compose.yml up -d

# Stop and remove the container (keeps the data volume)
docker compose -f backend/docker/docker-compose.yml down

# Stop and wipe the data (fresh database; Flyway recreates the schema on the next start)
docker compose -f backend/docker/docker-compose.yml down -v

# Inspect the data
docker exec -it trade-postgres psql -U postgres -d app -c "select account_id, name, email from app.account;"
```

> No `package.json` script wraps these commands. None of the `package.json` files in the repo (`frontend/`, `e2e/`) defines one.

## Run the tests

```bash
# Backend unit tests (*Test, Surefire)
cd backend && ./mvnw test

# Backend unit + integration tests (*IT, Failsafe) — this is what CI runs
cd backend && ./mvnw clean verify

# E2E (needs the frontend on 5174)
cd e2e && npx playwright test
cd e2e && npx playwright show-report      # open the HTML report
```

Notes:

- **The database must be up** before `./mvnw test` or `./mvnw verify`: `TradePlatformApplicationTests` and `SignupIT` start the Spring context, which connects to PostgreSQL and runs the Flyway migrations.
- `SignupIT` **starts the application itself** on port `8080` (`@SpringBootTest` with `DEFINED_PORT`). **Stop any running backend** before `./mvnw verify`, otherwise it fails with "Port 8080 was already in use".
- `AccountServiceTest` is a pure unit test: it uses `AccountRepositoryMemory`, with no Spring and no database.
- Use `clean` after renaming or deleting classes, so stale `.class` files in `target/` do not interfere.
- The Playwright `baseURL` is `http://localhost:5174` (`e2e/playwright.config.ts`), different from Vite's default `5173`.
- The frontend does not call the API yet (the signup form only shows `success`), so today the E2E test only needs the frontend. Once it calls the API, the backend and the database must be up too.
- The frontend has no unit-test setup yet (no Vitest in `frontend/package.json`).

## CI

`.github/workflows/ci.yml` runs `./mvnw -B -ntp verify` in `backend/` with JDK 17 on every pull request and push to `main`. It starts a **PostgreSQL 16 service container** (same user, password and database as `docker-compose.yml`, port `5432`), so Flyway creates the schema during the tests. On failure, the Surefire and Failsafe reports are uploaded as artifacts.

## Coverage

Coverage is meant to be measured with **JaCoCo** (target: above 80%), but **JaCoCo is not configured in `backend/pom.xml` yet**, so there is no coverage command today. Once the plugin is added, the report is expected at `backend/target/site/jacoco/index.html` after `./mvnw verify`.

## References

- **Code standards:** see the mandatory rule at the top of this file. The skill `code-standards` (`.claude/skills/code-standards/SKILL.md`) defines the coding rules for `backend/` (Java) and `frontend/` (TypeScript/React), with Bad/Good examples in `references/rules.md`.
- **Test automation:** see the mandatory rule at the top of this file. The skill `automated-tests` (`.claude/skills/automated-tests/SKILL.md`) defines the testing pyramid (unit, integration, E2E), tooling per module (JUnit 5/AssertJ/Mockito in `backend/`, Vitest/Testing Library in `frontend/`, Playwright in `e2e/`), naming (`*Test` / `*IT`), and the coverage target (>80%).
