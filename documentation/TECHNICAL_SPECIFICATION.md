# Technical Specification - Winter Olympics Backend API

Complete technical documentation for the Winter Olympics Management System backend API.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Authentication & Authorization](#authentication--authorization)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)
- [Data Transfer Objects (DTOs)](#data-transfer-objects-dtos)
- [Business Logic & Validation](#business-logic--validation)
- [Error Handling](#error-handling)
- [HTTP Status Codes](#http-status-codes)

---

## Overview

The Winter Olympics Backend API is built with Spring Boot 4.0.8 and provides REST endpoints for managing:
- User authentication and authorization (JWT-based)
- Athletes and country information
- Competitions (Biathlon and Ski Slalom)
- Competition registrations and eligibility validation
- Competition results and rankings
- Public statistics and medal tracking

**Key Technologies:**
- Java 21
- Spring Boot 4.0.8
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Maven

---

## Architecture

### Layered Architecture

```
┌─────────────────┐
│    Controllers  │ → REST Endpoints
├─────────────────┤
│    Services     │ → Business Logic
├─────────────────┤
│  Repositories   │ → Data Access (Spring Data JPA)
├─────────────────┤
│    Entities     │ → JPA Models
├─────────────────┤
│    Database     │ → PostgreSQL
└─────────────────┘
```

### Key Components

**Controllers:**
- `AuthController` - User authentication (login/register)
- `AthleteController` - Athlete management
- `CompetitionController` - Competition management (ADMIN only)
- `CountryController` - Country management
- `CompetitionRegistrationController` - Athlete registration
- `BiathlonResultController` - Biathlon results management
- `SlalomResultController` - Slalom results management
- `OlympicController` - Public statistics and medals

**Services:**
- `BiathlonService` - Biathlon calculations and rankings
- `SlalomService` - Slalom calculations, qualifications, and rankings
- `OlympicStatisticsService` - Medal calculations and statistics
- `JwtService` - JWT token generation and validation

**Security:**
- `SecurityConfig` - JWT-based Spring Security configuration
- `JwtAuthenticationFilter` - JWT token validation filter
- `DataInitializer` - Creates default admin user on startup

---

## Authentication & Authorization

### JWT Authentication Flow

```
1. User registers or logs in via /api/auth/register or /api/auth/login
2. Server validates credentials
3. Server generates JWT token (includes username, role, expiration)
4. Client includes token in Authorization header: "Bearer <token>"
5. JwtAuthenticationFilter validates token on each request
6. Request proceeds if token is valid
```

### Roles & Permissions

| Role   | Permissions |
|--------|------------|
| ADMIN  | Full access to all endpoints including competition and result management |
| ATHLETE | Access to athlete profile and competition registration endpoints; read-only public data |

### Authentication Endpoints

#### POST `/api/auth/register`
Register new athlete user account.

**Request:**
```json
{
  "username": "athlete1",
  "password": "<user-provided password>"
}
```

Public registration creates an ATHLETE account. It does not accept an athlete ID; the authenticated user must not be able to claim another athlete profile.

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "athlete1",
  "role": "ATHLETE"
}
```

#### POST `/api/auth/login`
Authenticate existing user.

**Request:**
```json
{
  "username": "athlete1",
  "password": "<user-provided password>"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "athlete1",
  "role": "ATHLETE"
}
```

### Admin Account Initialization

When the configured admin account needs to be created, the backend requires `ADMIN_INITIAL_PASSWORD` from its environment. Choose the username according to the configured initializer; do not place real credentials in source control or documentation. See [DEPLOYMENT.md](../DEPLOYMENT.md).

---

## API Endpoints

### Public Endpoints (No Auth Required)

#### GET `/api/auth/register`
#### GET `/api/auth/login`
#### GET `/api/olympics/statistics`
Get overall Olympic statistics and medal counts.

**Response (200 OK):**
```json
{
  "totalAthletes": 45,
  "totalCompetitions": 12,
  "countries": [
    {
      "countryName": "Norway",
      "goldMedals": 5,
      "silverMedals": 3,
      "bronzeMedals": 2,
      "totalMedals": 10
    }
  ],
  "totalMedals": 36
}
```

#### GET `/api/olympics/medals`
Get all medals awarded by country.

**Response (200 OK):**
```json
[
  {
    "countryName": "Norway",
    "goldMedals": 5,
    "silverMedals": 3,
    "bronzeMedals": 2,
    "totalMedals": 10
  },
  ...
]
```

#### GET `/api/olympics/medals/slalom/{competitionId}`
Get Slalom medals for specific competition.

**Response (200 OK):**
```json
[
  {
    "position": 1,
    "athleteName": "Mikaela Shiffrin",
    "country": "USA",
    "medal": "GOLD"
  },
  {
    "position": 2,
    "athleteName": "Petra Vlhova",
    "country": "Slovakia",
    "medal": "SILVER"
  },
  {
    "position": 3,
    "athleteName": "Katharina Liensberger",
    "country": "Austria",
    "medal": "BRONZE"
  }
]
```

#### GET `/api/olympics/medals/biathlon/{competitionId}`
Get Biathlon medals for specific competition.

**Response Format:** Same as Slalom medals

#### GET `/api/biathlon-results/ranking/{competitionId}`
Get Biathlon ranking (public endpoint).

**Response (200 OK):**
```json
[
  {
    "position": 1,
    "athleteName": "Tiril Eckhoff",
    "country": "Norway",
    "skiTime": 45.30,
    "penaltyTime": 0.0,
    "totalTime": 45.30,
    "finished": true
  },
  ...
]
```

#### GET `/api/slalom-results/ranking/{competitionId}`
Get Slalom ranking (public endpoint).

**Response (200 OK):**
```json
[
  {
    "position": 1,
    "athleteName": "Mikaela Shiffrin",
    "country": "USA",
    "firstRunTime": 52.45,
    "secondRunTime": 51.23,
    "totalTime": 103.68,
    "finished": true
  },
  ...
]
```

---

### Authenticated Endpoints - ATHLETE & ADMIN

#### GET `/api/athletes`
List all athletes (ATHLETE can only see themselves).

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Mikaela Shiffrin",
    "gender": "FEMALE",
    "dateOfBirth": "1995-03-13",
    "country": {
      "id": 1,
      "name": "USA"
    }
  },
  ...
]
```

#### GET `/api/athletes/{id}`
Get athlete details.

#### POST `/api/athletes`
Create new athlete (ATHLETE and ADMIN).

**Request:**
```json
{
  "name": "Mikaela Shiffrin",
  "countryId": 1,
  "gender": "FEMALE",
  "dateOfBirth": "1995-03-13"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Mikaela Shiffrin",
  "gender": "FEMALE",
  "dateOfBirth": "1995-03-13",
  "country": {
    "id": 1,
    "name": "USA"
  }
}
```

#### PUT `/api/athletes/{id}`
Update athlete information.

**Request:**
```json
{
  "name": "Updated Name",
  "countryId": 2,
  "gender": "FEMALE",
  "dateOfBirth": "1995-03-13"
}
```

#### DELETE `/api/athletes/{id}`
Delete athlete.

---

#### GET `/api/registrations`
List all competition registrations.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "athlete": {...},
    "competition": {...}
  },
  ...
]
```

#### POST `/api/registrations`
Register athlete for competition.

**Request:**
```json
{
  "athleteId": 1,
  "competitionId": 1
}
```

**Validation:**
- Athlete must exist
- Competition must exist
- Athlete's gender must match competition's gender
- Athlete's age must meet or exceed competition's minimum age

**Response (201 Created):**
```json
{
  "id": 1,
  "athlete": {...},
  "competition": {...}
}
```

#### DELETE `/api/registrations/{id}`
Unregister athlete from competition.

---

### Admin-Only Endpoints

#### GET `/api/countries`
List all countries.

#### POST `/api/countries`
Create new country.

**Request:**
```json
{
  "name": "Norway"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Norway"
}
```

#### PUT `/api/countries/{id}`
Update country.

#### DELETE `/api/countries/{id}`
Delete country.

---

#### GET `/api/competitions`
List all competitions.

#### POST `/api/competitions`
Create new competition (ADMIN only).

**Request:**
```json
{
  "name": "Women's 7.5km Sprint",
  "type": "BIATHLON",
  "gender": "FEMALE",
  "minimumAge": 18,
  "numberOfLaps": 3,
  "shootingAfterLaps": 2
}
```

**Validation:**
- For `BIATHLON`: Both `numberOfLaps` and `shootingAfterLaps` required; `shootingAfterLaps` ≤ `numberOfLaps`
- For `SKI_SLALOM`: Both `numberOfLaps` and `shootingAfterLaps` must be null

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Women's 7.5km Sprint",
  "type": "BIATHLON",
  "gender": "FEMALE",
  "minimumAge": 18,
  "numberOfLaps": 3,
  "shootingAfterLaps": 2
}
```

#### PUT `/api/competitions/{id}`
Update competition.

#### DELETE `/api/competitions/{id}`
Delete competition.

---

#### POST `/api/biathlon-results`
Record or update biathlon result (ADMIN only).

**Request:**
```json
{
  "registrationId": 1,
  "skiTime": 45.30,
  "misses": 2,
  "penaltyPerMiss": 1.0,
  "finished": true
}
```

**Response (201 Created):**
```json
{
  "athleteName": "Tiril Eckhoff",
  "competitionName": "Women's 7.5km Sprint",
  "skiTime": 45.30,
  "penaltyTime": 2.0,
  "totalTime": 47.30,
  "finished": true
}
```

---

#### POST `/api/slalom-results/first-run`
Record athlete's first run (ADMIN only).

**Request:**
```json
{
  "registrationId": 1,
  "time": 52.45,
  "finished": true
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "registration": {...},
  "firstRunTime": 52.45,
  "firstRunFinished": true,
  "secondRunTime": null,
  "secondRunFinished": false
}
```

#### POST `/api/slalom-results/second-run`
Record athlete's second run (ADMIN only).

**Validation:**
- Athlete must have finished first run
- Athlete must be in top 50% of finishers to qualify

**Request:**
```json
{
  "registrationId": 1,
  "time": 51.23,
  "finished": true
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "registration": {...},
  "firstRunTime": 52.45,
  "firstRunFinished": true,
  "secondRunTime": 51.23,
  "secondRunFinished": true
}
```

#### GET `/api/slalom-results/second-run/participants/{competitionId}`
Get athletes qualified for second run.

**Response (200 OK):**
```json
[
  {
    "athleteName": "Mikaela Shiffrin",
    "competitionName": "Women's Slalom",
    "firstRunTime": 52.45,
    "firstRunFinished": true
  },
  ...
]
```

#### GET `/api/slalom-results/second-run/start-order/{competitionId}`
Get second run start order (reverse of first run ranking).

**Response Format:** Same as participants

---

## Database Schema

### Tables

#### users
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `username` (VARCHAR, NOT NULL, UNIQUE)
- `password` (VARCHAR, NOT NULL)
- `role` (VARCHAR/ENUM, NOT NULL) - `ATHLETE`, `ADMIN`
- `athlete_id` (BIGINT, FK) - References athletes.id

#### athletes
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `name` (VARCHAR, NOT NULL)
- `country_id` (BIGINT, FK, NOT NULL) - References countries.id
- `gender` (VARCHAR/ENUM, NOT NULL) - `MALE`, `FEMALE`
- `date_of_birth` (DATE, NOT NULL)

#### countries
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `name` (VARCHAR, NOT NULL, UNIQUE)

#### competitions
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `name` (VARCHAR, NOT NULL)
- `type` (VARCHAR/ENUM, NOT NULL) - `BIATHLON`, `SKI_SLALOM`
- `gender` (VARCHAR/ENUM, NOT NULL) - `MALE`, `FEMALE`
- `minimum_age` (INTEGER, NOT NULL)
- `number_of_laps` (INTEGER) - NULL for SKI_SLALOM
- `shooting_after_laps` (INTEGER) - NULL for SKI_SLALOM

#### competition_registrations
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `athlete_id` (BIGINT, FK, NOT NULL) - References athletes.id
- `competition_id` (BIGINT, FK, NOT NULL) - References competitions.id

#### biathlon_results
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `registration_id` (BIGINT, FK, NOT NULL) - References competition_registrations.id
- `ski_time` (DOUBLE, NOT NULL)
- `misses` (INTEGER, NOT NULL)
- `penalty_per_miss` (DOUBLE, NOT NULL)
- `finished` (BOOLEAN, NOT NULL)

#### slalom_results
- `id` (BIGINT, PK, AUTO_INCREMENT)
- `registration_id` (BIGINT, FK, NOT NULL) - References competition_registrations.id
- `first_run_time` (DOUBLE)
- `first_run_finished` (BOOLEAN)
- `second_run_time` (DOUBLE)
- `second_run_finished` (BOOLEAN)

### Relationships

```
users (1) ──── (1) athletes
           ↓
countries (1) ──── (N) athletes
           ↓
competitions (1) ──── (N) competition_registrations ──── (N) athletes
           ↓
biathlon_results & slalom_results (1) ──── (1) competition_registrations
```

---

## Data Transfer Objects (DTOs)

### AuthRequest
```java
{
  "username": "string",
  "password": "string",
  "athleteId": "long (optional)"
}
```

### AuthResponse
```java
{
  "token": "string (JWT)",
  "username": "string",
  "role": "string (ATHLETE | ADMIN)"
}
```

### AthleteRequest
```java
{
  "name": "string",
  "countryId": "long",
  "gender": "string (MALE | FEMALE)",
  "dateOfBirth": "date (YYYY-MM-DD)"
}
```

### CompetitionRequest
```java
{
  "name": "string",
  "type": "string (BIATHLON | SKI_SLALOM)",
  "gender": "string (MALE | FEMALE)",
  "minimumAge": "integer",
  "numberOfLaps": "integer (required for BIATHLON, null for SKI_SLALOM)",
  "shootingAfterLaps": "integer (required for BIATHLON, null for SKI_SLALOM)"
}
```

### RegistrationRequest
```java
{
  "athleteId": "long",
  "competitionId": "long"
}
```

### BiathlonResultRequest
```java
{
  "registrationId": "long",
  "skiTime": "double",
  "misses": "integer",
  "penaltyPerMiss": "double",
  "finished": "boolean"
}
```

### BiathlonResultResponse
```java
{
  "athleteName": "string",
  "competitionName": "string",
  "skiTime": "double",
  "penaltyTime": "double (calculated)",
  "totalTime": "double (calculated)",
  "finished": "boolean"
}
```

### BiathlonRankingResponse
```java
{
  "position": "integer",
  "athleteName": "string",
  "country": "string",
  "skiTime": "double",
  "penaltyTime": "double",
  "totalTime": "double",
  "finished": "boolean"
}
```

### SlalomResultRequest
```java
{
  "registrationId": "long",
  "time": "double",
  "finished": "boolean"
}
```

### SlalomResultResponse
```java
{
  "athleteName": "string",
  "competitionName": "string",
  "firstRunTime": "double",
  "firstRunFinished": "boolean",
  "secondRunTime": "double",
  "secondRunFinished": "boolean"
}
```

### SlalomRankingResponse
```java
{
  "position": "integer",
  "athleteName": "string",
  "country": "string",
  "firstRunTime": "double",
  "secondRunTime": "double",
  "totalTime": "double (calculated)",
  "finished": "boolean"
}
```

### MedalResponse
```java
{
  "position": "integer",
  "athleteName": "string",
  "country": "string",
  "medal": "string (GOLD | SILVER | BRONZE)"
}
```

### CountryMedalResponse
```java
{
  "countryName": "string",
  "goldMedals": "integer",
  "silverMedals": "integer",
  "bronzeMedals": "integer",
  "totalMedals": "integer"
}
```

### OlympicStatisticsResponse
```java
{
  "totalAthletes": "integer",
  "totalCompetitions": "integer",
  "countries": "[CountryMedalResponse]",
  "totalMedals": "integer"
}
```

---

## Business Logic & Validation

### Athlete Registration Validation

When registering an athlete for a competition (`POST /api/registrations`):

1. **Existence Check:**
   - Athlete with given ID must exist
   - Competition with given ID must exist

2. **Gender Match:**
   - Athlete's gender must exactly match competition's gender
   - **Example:** Female athlete cannot register for male-only competition

3. **Age Requirement:**
   - Athlete's age (calculated from date of birth to current date) must be ≥ competition's minimum age
   - **Calculation:** `Period.between(athlete.dateOfBirth, LocalDate.now()).getYears()`

**Error Responses:**
- `409 Conflict` - Gender mismatch or age requirement not met
- `404 Not Found` - Athlete or competition not found

---

### Competition Configuration Validation

When creating or updating a competition (`POST/PUT /api/competitions`):

**For BIATHLON competitions:**
- `numberOfLaps` is **REQUIRED** (must not be null)
- `shootingAfterLaps` is **REQUIRED** (must not be null)
- Constraint: `shootingAfterLaps` must be ≤ `numberOfLaps`
- Both must be positive integers

**For SKI_SLALOM competitions:**
- `numberOfLaps` must be **NULL**
- `shootingAfterLaps` must be **NULL**
- If either is provided, request fails with `400 Bad Request`

**Error Response Example:**
```json
{
  "message": "Shooting lap cannot be greater than number of laps",
  "status": 400,
  "timestamp": "2026-10-07T10:30:00Z"
}
```

---

### Biathlon Results Calculation

**Penalty Time Calculation:**
```
penaltyTime = misses × penaltyPerMiss
```

**Total Time Calculation:**
```
totalTime = skiTime + penaltyTime
```

**Ranking Logic:**
1. Athletes sorted by `finished` status (finished = true comes first)
2. Within same finished status, sorted by `totalTime` (ascending)

**Example:**
| Athlete | Ski Time | Misses | Penalty Per Miss | Total Time | Finished | Position |
|---------|----------|--------|------------------|------------|----------|----------|
| Tiril   | 45.30    | 0      | 1.0              | 45.30      | true     | 1        |
| Eckhoff | 46.50    | 2      | 1.0              | 48.50      | true     | 2        |
| Smith   | 47.00    | -      | -                | -          | false    | 3        |

---

### Slalom Results Logic

**Two-Run Format:**
- Each athlete completes a first run
- Top 50% of finishers (by first run time) qualify for second run
- Final ranking based on combined time of both runs

**First Run (`POST /api/slalom-results/first-run`):**
- Records athlete's first run time
- If `finished = false`, athlete is disqualified (no second run)

**Qualification for Second Run:**
- Only athletes with `firstRunFinished = true` can participate
- Ranked by first run time (ascending)
- Top 50% of finishers advance
- **Example:** 20 finishers → 10 qualify for second run

**Second Run Start Order:**
- Reverse of first run ranking (worst performer starts first)
- Classic skiing format for fairness

**Final Ranking:**
- Based on combined time: `firstRunTime + secondRunTime`
- Athletes who didn't complete both runs are ranked below finishers
- Sorted by total time (ascending)

**Example Scenario:**
```
First Run Results:
1. Mikaela - 52.45s ✓
2. Petra - 52.78s ✓
3. Katharina - 53.12s ✓
4. Anna - DNF (Did Not Finish)
...

Qualifiers for Second Run (top 2 if top 50%): Mikaela, Petra
Start Order for Second Run: Mikaela, Petra (reverse order)

Second Run Times:
- Petra: 51.89s (Combined: 104.67s)
- Mikaela: 51.23s (Combined: 103.68s)

Final Ranking:
1. Mikaela - 103.68s
2. Petra - 104.67s
3. Katharina - 53.12s (DNF in 2nd)
4. Anna - DNF
```

---

## Error Handling

### Global Exception Handler

All exceptions are caught and formatted consistently via `GlobalExceptionHandler`.

### Common Error Scenarios

| Scenario | Status | Message |
|----------|--------|---------|
| Resource not found | 404 | `"Athlete not found"` |
| Athlete gender doesn't match competition | 409 | `"Athlete gender does not match competition gender"` |
| Athlete below minimum age | 409 | `"Athlete does not meet the minimum age requirement"` |
| Athlete not qualified for second run | 409 | `"Athlete is not qualified for the second run"` |
| First run not finished | 409 | `"Athlete did not finish the first run"` |
| Invalid JWT token | 401 | `"Invalid or expired token"` |
| Unauthorized access | 403 | `"Access denied"` |
| Validation error | 400 | `"Validation failed: ..."` |
| Username already exists | 409 | `"Username already exists"` |
| Invalid credentials | 401 | `"Invalid username or password"` |

### Error Response Format

```json
{
  "message": "Error description",
  "status": 400,
  "timestamp": "2026-10-07T10:30:00Z"
}
```

---

## HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| 200  | OK | Successful GET, PUT, POST without creation |
| 201  | Created | Successful POST that creates a resource |
| 204  | No Content | Successful DELETE |
| 400  | Bad Request | Invalid input, validation failure |
| 401  | Unauthorized | Missing/invalid JWT token |
| 403  | Forbidden | Insufficient permissions for endpoint |
| 404  | Not Found | Resource doesn't exist |
| 409  | Conflict | Business logic constraint violated |
| 500  | Internal Server Error | Unexpected server error |

---

## Configuration

Configuration is supplied through backend environment variables, with local-only defaults documented in `BE/.env.example`. Production should activate the `prod` profile, provide a strong private `JWT_SECRET`, database variables, and `CORS_ALLOWED_ORIGINS`, and use schema validation. See [DEPLOYMENT.md](../DEPLOYMENT.md) for the current variable names and commands. Never place secrets in frontend `VITE_*` variables.

---

## Integration Notes for Frontend

### Authentication Flow
1. User registers via `POST /api/auth/register` or logs in via `POST /api/auth/login`
2. Response includes JWT token
3. Store token in localStorage/sessionStorage
4. Include in all subsequent requests: `Authorization: Bearer <token>`

### Public Endpoints
- No authentication needed for:
  - `/api/olympics/**`
  - `/api/biathlon-results/ranking/**`
  - `/api/slalom-results/ranking/**`
  - `/api/auth/login`
  - `/api/auth/register`

### Admin-Only Endpoints
- Require ADMIN role:
  - `/api/competitions/**`
  - `/api/biathlon-results`
  - `/api/slalom-results/**`
  - `/api/countries/**`

### Athlete Endpoints
- Require ATHLETE or ADMIN role:
  - `/api/athletes/**`
  - `/api/registrations/**`
