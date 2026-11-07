# CLAUDE.md

## MANDATORY: load the `automated-tests` and `code-standards` skills before changing code

Whenever any code is created or changed, in any module, you MUST load both project skills **before touching the code**, with no exceptions (including small tweaks such as a one-line regex change):

- `automated-tests` (`.claude/skills/automated-tests/SKILL.md`): testing rules.
- `code-standards` (`.claude/skills/code-standards/SKILL.md`): coding rules (method size, parameters, early returns, constants, etc.).

Rules:

- Do not edit code or tests until both skills are loaded.
- Do not finish the task without checking that the behavior is covered by tests and that the code follows the standards.

## Project layout

| Module      | Stack                                 | Default port |
|-------------|---------------------------------------|--------------|
| `backend/`  | Java 17, Spring Boot (Maven wrapper)  | `8080`       |
| `frontend/` | React 19, Vite, TypeScript            | `5173` (Vite default) |
| `e2e/`      | Playwright                            | — (expects the frontend on `5174`, see below) |
| Database    | PostgreSQL 16 (Docker)                | `5432`       |

## Install dependencies

```bash
cd frontend && npm install
cd e2e && npm install && npx playwright install chromium   # first time only: downloads the browser
cd backend && ./mvnw dependency:resolve                    # optional: Maven resolves on the first build anyway
```

## Run the projects

```bash
# Backend (http://localhost:8080) — the database must be up first
cd backend && ./mvnw spring-boot:run

# Frontend (http://localhost:5173)
cd frontend && npm run dev

# Frontend on the port the E2E tests expect (http://localhost:5174)
cd frontend && npm run dev -- --port 5174
```

Other frontend scripts: `npm run build`, `npm run lint`, `npm run preview`.

## Database (Docker)

PostgreSQL runs on **port `5432`** (container `trade-postgres`; user `postgres`, password `123456`, database `app`). The schema is created from `backend/database/create.sql` on the container's first start.

```bash
# Start
docker compose -f backend/docker/docker-compose.yml up -d

# Stop and remove the container
docker compose -f backend/docker/docker-compose.yml down
```

> No `package.json` script wraps these commands. None of the `package.json` files in the repo (`frontend/`, `e2e/`) defines one.

## Run the tests

```bash
# Backend unit tests (*Test, Surefire)
cd backend && ./mvnw test

# Backend unit + integration tests (*IT, Failsafe) — this is what CI runs
cd backend && ./mvnw verify

# E2E (needs backend on 8080, database up and frontend on 5174)
cd e2e && npx playwright test
cd e2e && npx playwright show-report      # open the HTML report
```

Notes:

- `SignupIT` calls `http://localhost:8080`, so the backend must be running when you execute `./mvnw verify`.
- The Playwright `baseURL` is `http://localhost:5174` (`e2e/playwright.config.ts`), different from Vite's default `5173`.
- The frontend has no unit-test setup yet (no Vitest in `frontend/package.json`).

## Coverage

Coverage is meant to be measured with **JaCoCo** (target: above 80%), but **JaCoCo is not configured in `backend/pom.xml` yet**, so there is no coverage command today. Once the plugin is added, the report is expected at `backend/target/site/jacoco/index.html` after `./mvnw verify`.

## References

- **Code standards:** see the mandatory rule at the top of this file. The skill `code-standards` (`.claude/skills/code-standards/SKILL.md`) defines the coding rules for `backend/` (Java) and `frontend/` (TypeScript/React), with Bad/Good examples in `references/rules.md`.
- **Test automation:** see the mandatory rule at the top of this file. The skill `automated-tests` (`.claude/skills/automated-tests/SKILL.md`) defines the testing pyramid (unit, integration, E2E), tooling per module (JUnit 5/AssertJ/Mockito in `backend/`, Vitest/Testing Library in `frontend/`, Playwright in `e2e/`), naming (`*Test` / `*IT`), and the coverage target (>80%). It defines the testing pyramid (unit, integration, E2E), tooling per module (JUnit 5/AssertJ/Mockito in `backend/`, Vitest/Testing Library in `frontend/`, Playwright in `e2e/`), naming (`*Test` / `*IT`), and the coverage target (>80%).
