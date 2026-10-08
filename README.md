# Winter Olympics Management System

A Spring Boot REST API and React/Vite frontend for managing winter sports competitions, athletes, registrations, results, rankings, medals, and statistics.

## Local development

See [DEPLOYMENT.md](./DEPLOYMENT.md) for prerequisites, local PostgreSQL startup, backend/frontend commands, environment variables, and production configuration. Local defaults use frontend `http://localhost:5173`, backend `http://localhost:8081`, and PostgreSQL `localhost:5434`.

Create local environment files from `BE/.env.example` and `FE/.env.example` as needed. Set a private `JWT_SECRET` and `ADMIN_INITIAL_PASSWORD` in the backend environment; never commit real credentials. Admin initialization requires a configured initial password when a new admin account must be created.

## Project structure

```text
BE/  Spring Boot API and PostgreSQL Docker Compose configuration
FE/  React/Vite frontend
```

## API overview

- Public: authentication, competitions (read), public result rankings, medals, and statistics.
- ATHLETE: own profile and registrations, subject to backend ownership checks.
- ADMIN: athlete, competition, registration, country, result, and demo/test-data management.

See [the technical specification](./documentation/TECHNICAL_SPECIFICATION.md) for API contracts and [DEPLOYMENT.md](./DEPLOYMENT.md) for current configuration and operational commands.

## Build and test

```bash
cd BE && ./mvnw test
cd FE && npm run build
```

For database-backed local testing, start PostgreSQL as described in `DEPLOYMENT.md` first.
