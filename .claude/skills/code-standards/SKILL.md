---
name: code-standards
description: Coding standards for the trade-platform project (Java/Spring Boot in backend/, TypeScript/React in frontend/). Use this skill whenever you create, change or review production code, or when the user mentions code style, clean code, refactoring, naming, method size, parameters, constants, comments, nested ifs, ternaries, try/catch or readability, even if they don't say "standards". Load it before writing any production code, and after writing it to check that the code follows the rules.
---

# Code standards

These rules apply to all production code in `backend/` (Java) and `frontend/` (TypeScript/React). The full rules, with Bad/Good examples in both languages, are in [references/rules.md](references/rules.md). Read that file before writing or reviewing code.

## Workflow

1. **Read `references/rules.md`** before writing or changing production code.
2. **Write the code following the rules**: short methods (max 30 lines), at most 3 parameters, early returns, no nested ternaries, constants for meaningful values, no empty `try/catch`, no blank lines inside methods, comments only to explain _why_.
3. **Review what you wrote** against the list above before finishing. If a rule is broken, refactor (extract methods, introduce a `record` or typed object, use early returns) instead of leaving it.
4. **Keep tests green**: refactoring must not change behavior unless asked. Follow the `automated-tests` skill and run the tests after the change.

## Why these rules exist

Small, flat, well-named code is easy to read, test and change. Most of the rules push in the same direction: extract until each piece does one thing and its name says what.
