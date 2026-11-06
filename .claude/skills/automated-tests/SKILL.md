---
name: automated-tests
description: Testing rules for the trade-platform project (JUnit 5/AssertJ/Mockito in backend/, Vitest/Testing Library in frontend/, Playwright in e2e/). Use this skill whenever you create or change code that needs tests, or when the user mentions tests, unit/integration/E2E, TDD, mocks, stubs, coverage, JaCoCo, Surefire/Failsafe, `*Test`/`*IT` classes, Vitest or Playwright, even if they don't say "automated tests". Load it before writing any test, and after writing production code to check that its behavior is covered.
---

# Automated tests

This project requires automated tests for every piece of code created. The full rules, with examples, are in [references/rules.md](references/rules.md). Read that file before writing or reviewing tests: it holds the details (naming, tooling per module, boundary testing, coverage target) that this summary leaves out.

## Workflow

1. **Pick the test level** for the behavior you are covering:
   - Unit (`*Test`, `./mvnw test`) for domain rules; this is where every variation of a rule lives.
   - Integration (`*IT`, `./mvnw verify`) only for the wiring between layers: happy path plus one error case per rule.
   - Frontend component tests (Vitest + Testing Library) for what the user sees and does.
   - E2E (Playwright) only for critical flows such as signup, since they are slow and fragile.
2. **Read the section of `references/rules.md`** for the module you are touching (`backend/`, `frontend/` or `e2e/`).
3. **Write focused tests**: one behavior each, Given/When/Then, independent of each other, with unique data (e.g. UUID in emails) and mocks/stubs for anything external.
4. **Run them** with the commands in the root `CLAUDE.md` and report failures honestly, with the output.
5. **Check coverage** against the 80% target, prioritizing risky and frequently changing code. JaCoCo is the intended tool; if it is not configured in `backend/pom.xml`, say so instead of claiming a number.

## Why these rules exist

The test pyramid keeps the suite fast: many cheap unit tests, few integration tests, very few E2E. Independent and repeatable tests can run in parallel and in any order without flaking, which is what makes CI trustworthy.
