# Authentication and Security

## Login and Registration

The backend exposes `POST /api/auth/login` and `POST /api/auth/register`. Both accept a nonblank username and password. Registration creates an `ATHLETE` user and returns a JWT; it does not link that user to an athlete record. The DTO contains an optional `athleteId` property for compatibility, but registration rejects a non-null value. The test-data action creates linked demo ATHLETE accounts. The frontend Login form is implemented; the routed Register screen is currently a placeholder.

Passwords are encoded and checked with a Spring Security `PasswordEncoder` configured as BCrypt. The initial admin account uses username `admin`; its initial password must be provided through `ADMIN_INITIAL_PASSWORD` if that account is not already in the database. The initializer does not recreate or overwrite an existing admin account.

## JWT Requests

The backend signs tokens using HS256. Tokens contain the username as subject, a role claim, issue time, and expiration. `JWT_SECRET` must be configured and contain at least 32 UTF-8 bytes; `JWT_EXPIRATION_MS` must be positive and defaults to one day. Invalid signatures and expired tokens are rejected.

Send the token in the HTTP header:

```http
Authorization: Bearer <token>
```

The request filter reads the token subject, loads the user from PostgreSQL, and builds the Spring authority from the user's current database role. The role claim is included in the JWT, but route authority is resolved from the current user record.

## Authorization Summary

- Public routes: `/api/auth/**`, `/api/olympics/**`, Slalom/Biathlon `/ranking/**`, and `GET /api/competitions/**`.
- Competition writes, result writes and non-ranking result reads, `/api/admin/**`, athlete collection/id reads, athlete creation, and country writes require ADMIN.
- `/api/athletes/me` allows ATHLETE or ADMIN and returns the athlete linked to the authenticated username.
- Athlete `PUT` and `DELETE /api/athletes/{id}` are authenticated, then controller ownership checks allow an ATHLETE to act only on the linked athlete; ADMIN bypasses that ownership check.
- `/api/registrations/**` requires authentication. The controller returns all registrations to ADMIN and filters to the linked athlete for ATHLETE. Create/delete operations enforce ownership for ATHLETE; ADMIN may manage any registration.
- Country reads require authentication through the default security rule; they are not public endpoints.
- All unmatched API routes require authentication.

Frontend route guards mirror ADMIN/ATHLETE navigation but do not replace these API checks.

## Frontend Session Behavior

The React `AuthContext` stores token, username, and role in localStorage. The shared API client attaches the bearer token to authenticated requests. A 401 clears the stored session and triggers an auth-context event; a 403 leaves the session intact. Logout clears the session. No refresh-token endpoint is implemented.

## Production Configuration

Never expose `JWT_SECRET` in frontend code or `VITE_*` variables. Configure it as a Railway backend secret with at least 32 bytes. Set `CORS_ALLOWED_ORIGINS` to explicit trusted frontend origins; the backend rejects wildcard origins and allows credentials. Use a unique production key and rotate it deliberately because previously issued tokens become invalid after key rotation.

## Current Account-Linking Limitation

Public registration creates a user with no associated athlete. The test-data seeder is the only current workflow that creates linked athlete accounts; there is no general operation for linking an existing athlete to an account. `/api/athletes/me` returns 404 for accounts without an association.
