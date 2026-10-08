# Winter Olympics: Local and Production Setup

This is the operational quick reference. See [`docs/deployment.md`](docs/deployment.md) for the complete environment and schema notes.

## Local PostgreSQL

From `BE/`, create a local environment file and provide the local database password, a private JWT key, and an initial admin password if the admin account does not exist:

```sh
cd BE
cp .env.example .env
docker compose up -d postgres
```

Compose uses PostgreSQL 17 Alpine on host port `5434` by default and keeps data in `winter_olympics_data`. Do not remove that volume to troubleshoot connectivity.

## Start Services

```sh
# Backend (from BE/)
cd BE
./mvnw spring-boot:run

# Frontend (from FE/)
cd ../FE
cp .env.example .env.local
npm ci
npm run dev
```

Local URLs are `http://localhost:8081` for the backend and `http://localhost:5173` for Vite. The frontend example points `VITE_API_BASE_URL` to the local backend.

Build and test:

```sh
cd FE && npm run build
cd ../BE && ./mvnw test && ./mvnw -DskipTests package
```

## Deployment Environment

The deployment arrangement is Vercel for the frontend and Railway for the Spring Boot service and PostgreSQL. The Railway backend service is configured with repository root `/BE` per the deployment setup supplied for this project. No platform manifests or public URLs are checked in.

**Vercel frontend:** set `VITE_API_BASE_URL` to the deployed Railway API base URL. This value is visible to browser users; it must not contain secrets.

**Railway backend:** set `SPRING_PROFILES_ACTIVE=prod`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`, and `SERVER_PORT`. Reference the PostgreSQL service's `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, and `PGPASSWORD` to construct the JDBC URL. Railway's `DATABASE_URL` is `postgres://` and is not suitable as Spring's JDBC URL; `SPRING_DATASOURCE_URL` must start with `jdbc:postgresql://`.

Set `CORS_ALLOWED_ORIGINS` to exact Vercel origin(s); wildcard origins are rejected. Keep `JWT_SECRET` private and at least 32 bytes. `ADMIN_INITIAL_PASSWORD` is needed only if the initial `admin` account must be created. `JWT_EXPIRATION_MS` defaults to 86,400,000 milliseconds.

`DEMO_ATHLETE_PASSWORD` is required only when an administrator runs the reusable test-data action. It becomes the shared password for the generated `demo.*` ATHLETE accounts; usernames are returned by the action. Do not configure it for production use unless demo accounts are intentionally needed, and never use a real user password.

The ADMIN dashboard includes a destructive reset that permanently removes all application records and all accounts except the currently signed-in admin. It is protected by ADMIN authorization and a typed confirmation phrase, but it is not reversible. Do not use it against production data.

## Temporary Schema Bootstrap

The current `BE/src/main/resources/application-prod.properties` has `spring.jpa.hibernate.ddl-auto=update` as a temporary setting to initialize the empty Railway database. After the schema has been created and startup verified, change it back to `validate`. Do not leave `update` as the final production setting. The project has no versioned migration tool; see [`docs/deployment.md`](docs/deployment.md#production-schema-bootstrap--temporary-setting).
