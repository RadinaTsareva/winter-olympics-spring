# Testing

## Test Stack and Source Count

The backend uses JUnit Jupiter, Spring's MVC test support/`MockMvc`, Mockito, Spring Security Test, Hamcrest, and JSONPath. There are 49 `@Test` annotations across the checked-in test sources (41 controller tests, 4 security tests, 3 JWT tests, and 1 Spring application-context test). This is a count from source, not a claim that all tests pass in every environment.

## Coverage in the Repository

- `ControllerIntegrationTests` uses `@WebMvcTest` with mocked repositories/services to check request/response behavior for authentication, athletes, countries, competitions, registrations, Slalom/Biathlon results, medals/statistics, and the admin test-data controller.
- `AdminTestDataSecurityTests` checks access to the test-data route, public competition reads, and admin-only competition writes using MockMvc and Spring Security test users.
- `JwtServiceTests` checks generated subject/role/expiration, invalid and expired tokens, and fail-fast validation of short secrets/nonpositive expiration.
- `WinterOlympicsApplicationTests` is an application-context startup test; it needs a working configured datasource because JPA initializes at startup.

The MVC controller tests mock persistence. They do not replace a live PostgreSQL integration test, verify schema migrations, or exercise a deployed Vercel/Railway system end to end. There are no checked-in frontend test files or test script; `npm run build` runs TypeScript checking and Vite bundling.

## Commands

```sh
cd BE
./mvnw test
```

For the complete suite, make sure PostgreSQL is reachable using the backend environment. The test command may also depend on Byte Buddy agent support in the local Java runtime used by Mockito. The project does not configure a separate test database profile in the checked-in configuration; do not point tests at important production data.

Frontend build/type check:

```sh
cd FE
npm run build
```

## Demo/Test Data

`POST /api/admin/test-data` is admin-only and requires the existing backend authentication. The admin dashboard includes a confirmation dialog before sending the request. It creates/reuses the seeded athlete records and linked ATHLETE login accounts (`demo.<first>.<last>` usernames). Configure `DEMO_ATHLETE_PASSWORD` on the backend before using the endpoint; all seeded accounts share that password. The response and dashboard show the usernames, never the password. Existing matching accounts are reused without changing their password. Use a dedicated demo database and do not use this shared demo password for production accounts.

The transaction seeds/reuses these named demo records:

- 4 countries: Bulgaria, Norway, Sweden, Germany.
- 8 athletes (four male and four female) with deterministic country/gender/birth-date identities.
- 4 competitions: men's and women's Ski Slalom, and men's and women's Biathlon.
- 16 registrations and 8 results for each discipline (four registrations/results per event).

The service looks up records by identity before inserting, tracks created/reused counts, is transactionally scoped, and takes a transaction-scoped PostgreSQL advisory lock to serialize the lookup/insert flow across instances. It does not delete unrelated records. Existing result rows are reused and left as-is. Repeated requests are intended to add no duplicate matching demo records, but the endpoint still inserts persistent demonstration data on an empty or partially populated database. Run it only on a dedicated demo/test database, not production business data.

No live database double-run verification is represented by this documentation; the code implements the idempotent lookup behavior described above.

## Destructive Admin Reset

`POST /api/admin/reset-data` is ADMIN-only. The dashboard requires the administrator to type `DELETE ALL OLYMPICS DATA`, and the backend validates the same phrase. It deletes results, registrations, athletes, competitions, countries, and all other users in a transaction while preserving the currently authenticated ADMIN account. It does not drop the database or schema. This is irreversible; use only with a disposable dataset.
