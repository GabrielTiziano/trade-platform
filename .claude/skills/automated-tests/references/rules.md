# Automated Tests

## General rules

- **Whenever you write code**, make sure automated tests exist that cover the implemented behavior, following the coverage rules below.
- Apply the **Given, When, Then** or **Arrange, Act, Assert** pattern to keep tests well structured.

  ```java
  @Test
  void shouldRejectSignupWhenEmailIsAlreadyRegistered() {
      // Given (Arrange)
      when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);

      // When (Act)
      var thrown = catchThrowable(() -> signup.execute(input));

      // Then (Assert)
      assertThat(thrown).isInstanceOf(EmailAlreadyRegisteredException.class);
  }
  ```

- Tests must be **independent**: no coupling between them. Each one must be runnable in isolation or in parallel, in any order.
  - Bad: test B reads the user that test A created.
  - Good: each test creates the data it needs.
- Whenever tests share behavior, especially setup, put it in the setup method (`@BeforeEach` in the backend, `beforeEach` in TypeScript).

  ```ts
  let onSubmit: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    onSubmit = vi.fn();
    render(<SignupForm onSubmit={onSubmit} />);
  });
  ```

- Whenever tests use external resources, such as database connections, release them in the teardown method (`@AfterEach` in the backend, `afterEach` in TypeScript).

  ```java
  @AfterEach
  void tearDown() {
      connection.close();
  }
  ```

- Tests must always be **repeatable**: use test patterns such as **stub** or **mock** to avoid depending on external data that may change, such as API calls. Generate unique data (e.g. email with a UUID) when the test creates records.

  ```java
  String email = "user-" + UUID.randomUUID() + "@example.com";
  ```

- Write **unit** tests for domain concepts, **integration** tests when more than one layer is involved, and **E2E** tests for the complete end-to-end flow, including the frontend.

  | Type        | Example                                         | Suffix / location          |
  |-------------|-------------------------------------------------|----------------------------|
  | Unit        | `ValidateCpf` rejects an invalid CPF            | `ValidateCpfTest`          |
  | Integration | `POST /signup` persists the user in the database| `SignupIT`                 |
  | E2E         | User fills the signup form in the browser       | `e2e/tests/signup.spec.ts` |

- Tests must focus on **specific behaviors** and be well documented. Avoid tests that embrace too much and take longer to run.
  - Bad: one `shouldSignup` test that checks validation, persistence, email sending and the UI.
  - Good: one test per behavior (`shouldPersistUser`, `shouldRejectInvalidCpf`, ...).
- There must always be **clear expectations** for each piece of data being validated.

  ```java
  assertThat(account.getId()).isNotNull();
  assertThat(account.getEmail()).isEqualTo(email);
  assertThat(account.getStatus()).isEqualTo(Status.ACTIVE);
  ```

- Aim for the highest coverage possible with good cost-benefit: coverage **above 80%**, prioritizing what carries the most risk and changes the most, is adequate.

## Backend (`backend/`)

- Use **JUnit 5** to build and run tests, **AssertJ** for assertions and **Mockito** to implement test patterns such as **stub**, **spy** and **mock**.

  ```java
  @ExtendWith(MockitoExtension.class)
  class SignupTest {
      @Mock AccountRepository accountRepository;   // mock
      @InjectMocks Signup signup;

      @Test
      void shouldSaveAccount() {
          signup.execute(validInput());

          verify(accountRepository).save(any(Account.class));
      }
  }
  ```

- Use `@ParameterizedTest` (with `@ValueSource`, `@CsvSource`, `@MethodSource`, `@NullAndEmptySource`) when the same behavior must be validated with different data.

  ```java
  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "123", "111.111.111-11"})
  void shouldRejectInvalidCpf(String cpf) {
      assertThat(ValidateCpf.isValid(cpf)).isFalse();
  }
  ```

- For each rule, test both sides of the boundary: invalid cases (each one breaking a single rule) and valid cases (limits and variations).

  Example for a rule "name must have between 3 and 50 characters":

  | Input            | Expected | Why                   |
  |------------------|----------|-----------------------|
  | `"ab"` (2 chars) | invalid  | just below the lower limit |
  | `"abc"` (3)      | valid    | lower limit           |
  | 50 chars         | valid    | upper limit           |
  | 51 chars         | invalid  | just above the upper limit |

- The test package must mirror the package of the tested class.
  - `src/main/java/com/tradeplatform/signup/ValidateCpf.java` → `src/test/java/com/tradeplatform/signup/ValidateCpfTest.java`
- Naming:
  - Unit tests end with `Test` (e.g. `ValidateCpfTest`) and run with `./mvnw test` (Surefire).
  - Integration tests end with `IT` (e.g. `SignupIT`) and run with `./mvnw verify` (Failsafe).
- Integration tests must cover only the wiring between layers (happy path and one error case per rule). The variations of each rule belong in the unit tests.
  - `SignupIT`: valid signup returns `201` and the row exists in the DB; duplicated email returns `409`. Whether a CPF has 10, 11 or 12 digits is tested in `ValidateCpfTest`.
- Coverage is measured with **JaCoCo**.

## Frontend (`frontend/`)

- Use **Vitest** with **Testing Library** for component tests, and Vitest itself (`vi.fn`, `vi.spyOn`, `vi.mock`) for stub, spy and mock.

  ```ts
  vi.mock("../api/signup", () => ({ signup: vi.fn().mockResolvedValue({ id: "1" }) }));
  ```

- Test the behavior visible to the user (what appears on screen and how they interact), not implementation details of the components.

  ```tsx
  // Good: what the user sees and does
  await userEvent.type(screen.getByLabelText(/email/i), "ana@example.com");
  await userEvent.click(screen.getByRole("button", { name: /sign up/i }));
  expect(await screen.findByText(/account created/i)).toBeInTheDocument();

  // Bad: internal state / implementation details
  expect(component.state.isSubmitting).toBe(false);
  ```

## E2E (`e2e/`)

- Use **Playwright** for end-to-end tests.
- Use the `baseURL` configured in `playwright.config.ts` and navigate with relative paths (e.g. `page.goto("/")`), with no hardcoded URLs in the tests.

  ```ts
  await page.goto("/");               // good
  await page.goto("http://localhost:5173/"); // bad
  ```

- Write few E2E tests, only for critical flows (e.g. signup), since they are the slowest and most fragile of the pyramid.

  ```ts
  test("user signs up successfully", async ({ page }) => {
    await page.goto("/");
    await page.getByLabel("Email").fill(`user-${crypto.randomUUID()}@example.com`);
    await page.getByRole("button", { name: "Sign up" }).click();
    await expect(page.getByText("Account created")).toBeVisible();
  });
  ```
