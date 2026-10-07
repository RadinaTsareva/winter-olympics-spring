# Winter Olympics Management System - Backend

A Spring Boot REST API for managing winter Olympic events, athletes, competitions, and results. This is the **backend service** - a separate frontend application will consume this API.

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21+
- Maven 3.6+

### 1. Start Database
```bash
docker-compose up -d
```

### 2. Run Application
```bash
./mvnw spring-boot:run
```

Application starts on `http://localhost:8080`

### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## Key Features

- ✅ JWT Authentication & Authorization (ADMIN, ATHLETE roles)
- ✅ Athlete & Competition Management
- ✅ Biathlon & Ski Slalom Results Tracking
- ✅ Automatic Ranking & Medal Calculations
- ✅ Public Statistics & Medal API
- ✅ PostgreSQL Database with Docker Support

## Project Structure

```
src/main/java/com/example/winter_olympics/
├── config/          # Security, JWT, initialization
├── controller/      # REST endpoints
├── entity/          # JPA entities
├── repository/      # Data access
├── service/         # Business logic
└── dto/             # Data transfer objects
```

## Available Endpoints

### Public (No Auth Required)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login
- `GET /api/olympics/statistics` - View medal counts
- `GET /api/olympics/medals` - View all medals
- `GET /api/biathlon-results/ranking/{competitionId}` - Biathlon rankings
- `GET /api/slalom-results/ranking/{competitionId}` - Slalom rankings

### Authenticated (ATHLETE + ADMIN)
- `GET /api/athletes` - List athletes
- `POST /api/athletes` - Create athlete
- `PUT /api/athletes/{id}` - Update athlete
- `DELETE /api/athletes/{id}` - Delete athlete
- `GET /api/registrations` - List registrations
- `POST /api/registrations` - Register for competition

### Admin Only
- `GET/POST/PUT/DELETE /api/competitions` - Manage competitions
- `GET/POST/PUT/DELETE /api/countries` - Manage countries
- `POST /api/biathlon-results` - Record biathlon results
- `POST /api/slalom-results/first-run` - Record first run
- `POST /api/slalom-results/second-run` - Record second run

## Configuration

### Default Admin User
Auto-created on startup:
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** `ADMIN`

### Database
PostgreSQL running on `localhost:5434`:
- **Database:** `winter_olympics`
- **User:** `olympics`
- **Password:** `olympics`

## Development

### Build
```bash
./mvnw clean package
```

### Test
```bash
./mvnw test
```

### Stop Database
```bash
docker-compose down
```

### Clean Up (Remove Data)
```bash
docker-compose down -v
```

## Documentation

📖 **For complete API reference and technical details, see:**
- [TECHNICAL_SPECIFICATION.md](./documentation/TECHNICAL_SPECIFICATION.md)

Contains:
- Complete API endpoint documentation with request/response examples
- Database schema and relationships
- Authentication & authorization details
- Business logic and validation rules
- Error handling and status codes
- DTOs and data structures

## Technology Stack

- Java 21
- Spring Boot 4.0.8
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Maven
- Docker & Docker Compose

## Support

- See [TECHNICAL_SPECIFICATION.md](./documentation/TECHNICAL_SPECIFICATION.md) for complete API documentation
- Spring Boot docs: https://docs.spring.io/spring-boot/
- PostgreSQL docs: https://www.postgresql.org/docs/
- Maven docs: https://maven.apache.org/guides/
- Docker docs: https://docs.docker.com/

