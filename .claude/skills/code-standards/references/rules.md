# Coding standards

These rules apply to all production code in `backend/` (Java) and `frontend/` (TypeScript/React). Follow them when writing new code and when changing existing code.

## Rules

- **Do not write methods longer than 30 lines.** Extract smaller methods with names that describe what they do. In React, extract smaller components and custom hooks.

  ```java
  // Bad: one method validates, persists and builds the response
  ResponseEntity<?> signup(SignupInput input) { /* 45 lines */ }

  // Good
  ResponseEntity<?> signup(SignupInput input) {
      validate(input);
      Account account = accountRepository.save(toAccount(input));
      return created(account);
  }
  ```

  ```tsx
  // Bad: one component with the form state, validation, request and markup
  function SignupPage() { /* 80 lines */ }

  // Good
  function SignupPage() {
    const { submit, error } = useSignup();
    return <SignupForm onSubmit={submit} error={error} />;
  }
  ```

- **Do not pass more than 3 parameters**; prefer objects (a `record` in Java, a typed object in TypeScript).

  ```java
  // Bad
  Account create(String name, String email, String document, String password) { ... }

  // Good
  record SignupInput(String name, String email, String document, String password) {}
  Account create(SignupInput input) { ... }
  ```

  ```ts
  // Bad
  function signup(name: string, email: string, document: string, password: string) { ... }

  // Good
  interface SignupInput {
    name: string;
    email: string;
    document: string;
    password: string;
  }
  function signup(input: SignupInput) { ... }
  ```

- **Declare variables as close as possible** to where they are used.

  ```java
  // Bad
  String normalized = document.replaceAll("[.\\-]", "");
  if (name == null) throw new InvalidNameException();
  return repository.existsByDocument(normalized);

  // Good
  if (name == null) throw new InvalidNameException();
  String normalized = document.replaceAll("[.\\-]", "");
  return repository.existsByDocument(normalized);
  ```

- **Never use an empty `try/catch`.** Handle the exception, rethrow it with context, or let it propagate.

  ```java
  // Bad
  try { ... } catch (Exception e) { }

  // Good
  try { ... } catch (IOException e) {
      throw new AccountStorageException("Failed to save account " + accountId, e);
  }
  ```

  ```ts
  // Bad
  try { await signup(input); } catch (e) {}

  // Good
  try {
    await signup(input);
  } catch (error) {
    setErrorMessage(SIGNUP_FAILED_MESSAGE);
  }
  ```

- **Do not leave blank lines inside methods and functions.** If a method needs blank lines to be readable, it is doing too much: extract methods instead.
  - Exception: in tests, a blank line may separate the Given / When / Then sections (see the `automated-tests` skill).

  ```java
  // Bad
  Account save(SignupInput input) {
      validate(input);

      Account account = toAccount(input);

      return repository.save(account);
  }

  // Good
  Account save(SignupInput input) {
      validate(input);
      Account account = toAccount(input);
      return repository.save(account);
  }
  ```

- **Avoid unnecessary comments**: the code must express its intention clearly and objectively. Prefer good names over comments that explain what the code does; keep comments only to explain _why_ something non-obvious is done.

  ```java
  // Bad
  // checks if the cpf is valid
  boolean r = ValidateCpf.validate(c);

  // Good
  boolean isValidCpf = ValidateCpf.validate(cpf);
  ```

  ```ts
  // Good: the comment explains why, not what
  // The API rejects formatted documents, so send digits only.
  const document = form.document.replace(/\D/g, "");
  ```

- **Always create constants** for numbers or strings with important meaning.

  ```java
  // Bad
  return ResponseEntity.status(422).body(Map.of("message", "Invalid name"));

  // Good
  private static final int UNPROCESSABLE_ENTITY = 422;
  private static final String INVALID_NAME = "Invalid name";
  ```

  ```ts
  // Bad
  if (password.length < 8) return "Password too short";

  // Good
  const MIN_PASSWORD_LENGTH = 8;
  const PASSWORD_TOO_SHORT = "Password too short";
  if (password.length < MIN_PASSWORD_LENGTH) return PASSWORD_TOO_SHORT;
  ```

- **Do not nest more than 2 levels of `if/else`**; prefer **early returns**.

  ```java
  // Bad
  if (input != null) {
      if (ValidateName.validate(input.name())) {
          return save(input);
      } else {
          throw new InvalidNameException();
      }
  }

  // Good
  if (input == null) throw new IllegalArgumentException("Input is required");
  if (!ValidateName.validate(input.name())) throw new InvalidNameException();
  return save(input);
  ```

  ```ts
  // Bad
  function validate(input: SignupInput | null) {
    if (input) {
      if (isValidName(input.name)) {
        return save(input);
      } else {
        throw new Error("Invalid name");
      }
    }
  }

  // Good
  function validate(input: SignupInput | null) {
    if (!input) throw new Error("Input is required");
    if (!isValidName(input.name)) throw new Error("Invalid name");
    return save(input);
  }
  ```

- **Do not nest ternaries.**

  ```ts
  // Bad
  const label =
    status === "active"
      ? "Active"
      : status === "blocked"
        ? "Blocked"
        : "Inactive";

  // Good
  const LABELS = { active: "Active", blocked: "Blocked" } as const;
  const label = LABELS[status] ?? "Inactive";
  ```

  ```java
  // Bad
  String label = active ? "Active" : blocked ? "Blocked" : "Inactive";

  // Good
  private String labelFor(Status status) {
      return switch (status) {
          case ACTIVE -> "Active";
          case BLOCKED -> "Blocked";
          default -> "Inactive";
      };
  }
  ```
