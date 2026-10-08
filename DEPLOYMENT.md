# Winter Olympics deployment notes

Do not commit environment files or put backend secrets in `VITE_*` variables. Vite variables are shipped to browsers.

## Local development

The existing project uses PostgreSQL on host port `5434`, the backend on `8081`, and Vite on `5173`. Port `8080` was previously identified as occupied, so keep the current `8081` setting unless that conflict has been resolved.

### PostgreSQL

From `BE/`, create local configuration and set private values:

```sh
cp .env.example .env
```

Set `DATABASE_PASSWORD` to the local PostgreSQL password. Generate a development JWT key with `openssl rand -base64 48` and assign it to `JWT_SECRET`. Set `ADMIN_INITIAL_PASSWORD` if the database does not already contain the `admin` account.

Start the provided local database container:

```sh
docker compose up -d postgres
```

If an existing PostgreSQL instance already owns port `5434`, reuse that instance or set `POSTGRES_PORT` and update `DATABASE_URL` to the same port. Do not remove its container or volume to resolve a port conflict.

### Backend

The backend reads `BE/.env` when started with `BE/` as the working directory:

```sh
cd BE
./mvnw spring-boot:run
```

Local API base: `http://localhost:8081`.

### Frontend

`FE/.env.example` points Vite at the local backend. Copy it to a Vite local environment file:

```sh
cd FE
cp .env.example .env.local
npm install
npm run dev
```

Local frontend: `http://localhost:5173`.

Build the frontend with `npm run build` from `FE/`.

Run backend tests and package the backend with:

```sh
cd BE
./mvnw test
./mvnw -DskipTests package
```

The test suite's full application-context test needs the configured PostgreSQL database. Do not use a production database for test-data verification.

## Production configuration

Set the following backend environment variables in the deployment platform's secret/configuration manager. Do not add production values to source control.

| Variable | Purpose |
| --- | --- |
| `DATABASE_URL` | JDBC PostgreSQL URL, for example `jdbc:postgresql://<database-host>:5432/<database-name>` |
| `DATABASE_USERNAME` | Database login name |
| `DATABASE_PASSWORD` | Database password; keep it in the platform's secret store |
| `JWT_SECRET` | Random secret of at least 32 bytes; generate with `openssl rand -base64 48` |
| `JWT_EXPIRATION_MS` | Token lifetime in milliseconds; defaults to 86400000 |
| `CORS_ALLOWED_ORIGINS` | Comma-separated explicit frontend origins, for example `<production-frontend-url>`; wildcard origins are rejected |
| `SERVER_PORT` | Backend listening port; defaults to 8081 |
| `SPRING_PROFILES_ACTIVE` | Set to `prod` to use safe Hibernate schema validation |
| `ADMIN_INITIAL_PASSWORD` | Required only when the initial `admin` account does not yet exist |

Production frontend: `<production frontend URL>`  
Production backend: `<production backend URL>`

Do not replace these placeholders until the actual hosting URLs are known. Set the frontend build variable `VITE_API_BASE_URL` to the backend base URL; this value is public and must never contain secrets.

The `prod` Spring profile sets `spring.jpa.hibernate.ddl-auto=validate` and disables SQL logging. It does not create or migrate the schema. This repository currently has no versioned database migration tool, so provision/migrate the production schema through the chosen database change process before starting the production profile. Local configuration keeps Hibernate `update` for development convenience; avoid `create` and `create-drop` in any persistent environment.

The backend fails startup when `JWT_SECRET` is missing/too short, or when the initial admin account needs creation but `ADMIN_INITIAL_PASSWORD` is missing. CORS must list exact trusted origins. Public pages need no JWT; protected API routes continue to require the existing bearer token.

## Demo data

`POST /api/admin/test-data` is for a controlled demo/test database only. It requires an ADMIN JWT, inserts missing matching records transactionally, and reuses existing demo records. Do not use it to prepare or mutate production business data.
