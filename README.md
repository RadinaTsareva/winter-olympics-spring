# Winter Olympics Management System

A Spring Boot RESTful API for managing winter Olympics competitions, athletes, and results. This application tracks athletes participating in winter sports competitions such as Biathlon and Slalom, and manages their registration and performance results.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Setup](#setup)
  - [Running with Docker](#running-with-docker)
  - [Building and Running Locally](#building-and-running-locally)
- [API Endpoints](#api-endpoints)
- [Example API Requests and Responses](#example-api-requests-and-responses)
- [Database Schema](#database-schema)
- [HTTP Status Codes](#http-status-codes)
- [Error Handling](#error-handling)
- [Business Logic and Validation](#business-logic-and-validation)
- [Configuration](#configuration)
- [Development](#development)
- [Testing](#testing)
- [Monitoring and Health Checks](#monitoring-and-health-checks)
- [Security](#security)
- [Troubleshooting](#troubleshooting)
- [References](#references)

## Overview

The Winter Olympics Management System is a comprehensive backend solution for organizing and tracking winter Olympic events. It provides functionality for managing athletes, competitions, registration, and results tracking for various winter sports disciplines.

## Features

- **Athlete Management**: Create, retrieve, update, and delete athlete profiles
- **Competition Management**: Manage different winter Olympics competitions
- **Competition Registration**: Handle athlete registration for competitions
- **Results Tracking**: Record and retrieve results for:
  - Biathlon events
  - Slalom events
- **Country Management**: Maintain athlete country information
- **RESTful API**: Complete REST API for all operations
- **Security**: Spring Security integration for protected endpoints
- **Validation**: Input validation using Hibernate Validator
- **Distributed Tracing**: Micrometer tracing support for monitoring
- **Database Persistence**: PostgreSQL integration with JPA/Hibernate

## Technology Stack

- **Java**: 21
- **Framework**: Spring Boot 4.0.8
- **ORM**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL 17
- **Security**: Spring Security
- **Container**: Docker & Docker Compose
- **Build Tool**: Maven
- **Additional Libraries**:
  - Lombok (code generation)
  - Micrometer Tracing (distributed tracing)
  - Spring Actuator (application metrics)
  - Spring Validation

## Prerequisites

### Option 1: Using Docker
- Docker
- Docker Compose

### Option 2: Local Setup
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use Docker for the database)

## Quick Start

Get the application running in 3 steps:

1. **Start the database** (requires Docker & Docker Compose):
   ```bash
   cd /Users/radinatsareva/Desktop/Uni/projects/winter-olympics
   docker-compose up -d
   ```

2. **Build and run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Test the API** (using curl or Postman):
   ```bash
   # Get all countries
   curl http://localhost:8080/api/countries
   
   # Create a country
   curl -X POST http://localhost:8080/api/countries \
     -H "Content-Type: application/json" \
     -d '{"name": "Norway"}'
   ```

The application will be running at `http://localhost:8080`.

## Project Structure

```
winter-olympics/
├── src/
│   ├── main/
│   │   ├── java/com/example/winter_olympics/
│   │   │   ├── WinterOlympicsApplication.java          # Main application entry point
│   │   │   ├── config/
│   │   │   │   ├── GlobalExceptionHandler.java         # Global exception handling
│   │   │   │   └── SecurityConfig.java                 # Spring Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── AthleteController.java              # Athlete endpoints
│   │   │   │   ├── BiathlonResultController.java       # Biathlon results endpoints
│   │   │   │   ├── CompetitionController.java          # Competition endpoints
│   │   │   │   ├── CompetitionRegistrationController.java # Registration endpoints
│   │   │   │   ├── CountryController.java              # Country endpoints
│   │   │   │   └── SlalomResultController.java         # Slalom results endpoints
│   │   │   ├── entity/
│   │   │   │   ├── Athlete.java                        # Athlete entity
│   │   │   │   ├── Competition.java                    # Competition entity
│   │   │   │   ├── CompetitionRegistration.java        # Registration entity
│   │   │   │   ├── BiathlonResult.java                 # Biathlon result entity
│   │   │   │   ├── SlalomResult.java                   # Slalom result entity
│   │   │   │   ├── Country.java                        # Country entity
│   │   │   │   ├── Gender.java                         # Gender enum
│   │   │   │   └── CompetitionType.java                # Competition type enum
│   │   │   ├── repository/
│   │   │   │   ├── AthleteRepository.java              # Athlete data access
│   │   │   │   ├── CompetitionRepository.java          # Competition data access
│   │   │   │   ├── CompetitionRegistrationRepository.java
│   │   │   │   ├── BiathlonResultRepository.java       # Biathlon results data access
│   │   │   │   ├── SlalomResultRepository.java         # Slalom results data access
│   │   │   │   └── CountryRepository.java              # Country data access
│   │   │   ├── service/
│   │   │   │   ├── BiathlonService.java                # Biathlon business logic
│   │   │   │   └── SlalomService.java                  # Slalom business logic
│   │   │   └── dto/
│   │   │       ├── AthleteRequest.java                 # Athlete request DTO
│   │   │       ├── CompetitionRequest.java             # Competition request DTO
│   │   │       ├── RegistrationRequest.java            # Registration request DTO
│   │   │       ├── BiathlonResultRequest.java          # Biathlon result request DTO
│   │   │       ├── BiathlonResultResponse.java         # Biathlon result response DTO
│   │   │       ├── SlalomResultRequest.java            # Slalom result request DTO
│   │   │       ├── SlalomResultResponse.java           # Slalom result response DTO
│   │   │       └── SlalomRankingResponse.java          # Slalom ranking response DTO
│   │   └── resources/
│   │       └── application.properties                  # Application configuration
│   └── test/
│       └── java/com/example/winter_olympics/
│           └── WinterOlympicsApplicationTests.java     # Integration tests
├── docker-compose.yml                                   # Docker Compose configuration
├── pom.xml                                              # Maven configuration
├── mvnw                                                 # Maven wrapper (Linux/Mac)
└── mvnw.cmd                                             # Maven wrapper (Windows)
```

## Getting Started

### Setup

#### Option 1: Docker Compose (Recommended)

1. **Clone or navigate to the project directory**:
   ```bash
   cd /Users/radinatsareva/Desktop/Uni/projects/winter-olympics
   ```

2. **Start the PostgreSQL database using Docker Compose**:
   ```bash
   docker-compose up -d
   ```
   This will:
   - Start a PostgreSQL 17 Alpine container
   - Create the database `winter_olympics`
   - Set credentials: username `olympics`, password `olympics`
   - Expose the database on port 5434

3. **Build the application**:
   ```bash
   ./mvnw clean package
   ```

4. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Verify the application**:
   - Application should be accessible at `http://localhost:8080`
   - Check health at `http://localhost:8080/actuator/health`

#### Option 2: Local Setup with Maven

1. **Ensure PostgreSQL is running** with the following configuration:
   - Database: `winter_olympics`
   - Username: `olympics`
   - Password: `olympics`
   - Port: `5434`

2. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

3. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### Stopping Docker Services

```bash
docker-compose down
```

To also remove the data volume:
```bash
docker-compose down -v
```

## API Endpoints

### Athletes
- `GET /api/athletes` - List all athletes
- `GET /api/athletes/{id}` - Get athlete by ID
- `POST /api/athletes` - Create new athlete (requires valid country ID, gender, name, and date of birth)
- `PUT /api/athletes/{id}` - Update athlete information
- `DELETE /api/athletes/{id}` - Delete athlete

**Athlete Fields:**
- `id`: Long (auto-generated)
- `name`: String (required)
- `country`: Country object (required)
- `gender`: Enum - `MALE` or `FEMALE` (required)
- `dateOfBirth`: LocalDate (required)

### Countries
- `GET /api/countries` - List all countries
- `GET /api/countries/{id}` - Get country by ID
- `POST /api/countries` - Create new country
- `PUT /api/countries/{id}` - Update country name
- `DELETE /api/countries/{id}` - Delete country

**Country Fields:**
- `id`: Long (auto-generated)
- `name`: String (required, unique)

### Competitions
- `GET /api/competitions` - List all competitions
- `GET /api/competitions/{id}` - Get competition by ID
- `POST /api/competitions` - Create new competition (with validation based on type)
- `PUT /api/competitions/{id}` - Update competition details
- `DELETE /api/competitions/{id}` - Delete competition

**Competition Fields:**
- `id`: Long (auto-generated)
- `name`: String (required)
- `type`: Enum - `BIATHLON` or `SKI_SLALOM` (required)
- `gender`: Enum - `MALE` or `FEMALE` (required)
- `minimumAge`: Integer (required)
- `numberOfLaps`: Integer (required for BIATHLON, must be null for SKI_SLALOM)
- `shootingAfterLaps`: Integer (required for BIATHLON, must be null for SKI_SLALOM)

**Competition Validation Rules:**
- For BIATHLON: `numberOfLaps` and `shootingAfterLaps` are required, and `shootingAfterLaps` cannot exceed `numberOfLaps`
- For SKI_SLALOM: `numberOfLaps` and `shootingAfterLaps` must be null

### Competition Registration
- `GET /api/registrations` - List all competition registrations
- `POST /api/registrations` - Register athlete for competition
- `DELETE /api/registrations/{id}` - Unregister athlete from competition

**Registration Validation:**
- Athlete gender must match competition gender
- Athlete age (calculated from date of birth) must meet or exceed competition minimum age

### Biathlon Results
- `POST /api/biathlon-results` - Record or update biathlon result
- `GET /api/biathlon-results/ranking/{competitionId}` - Get biathlon ranking for a specific competition

**Biathlon Result Fields:**
- `skiTime`: Double (required)
- `misses`: Integer (required)
- `penaltyPerMiss`: Double (required)
- `finished`: Boolean (required)

**Biathlon Ranking Response:**
Returns athletes ranked by:
1. Finished status
2. Total time (ski time + penalties)

### Slalom Results
- `POST /api/slalom-results/first-run` - Record athlete's first run
- `POST /api/slalom-results/second-run` - Record athlete's second run (requires first run to be finished and athlete qualified)
- `GET /api/slalom-results/second-run/participants/{competitionId}` - Get athletes qualified for second run
- `GET /api/slalom-results/second-run/start-order/{competitionId}` - Get second run start order (reverse order of first run)
- `GET /api/slalom-results/ranking/{competitionId}` - Get final slalom ranking

**Slalom Result Fields:**
- `firstRunTime`: Double
- `firstRunFinished`: Boolean
- `secondRunTime`: Double
- `secondRunFinished`: Boolean

**Slalom Second Run Qualification:**
- Only top 50% of athletes from first run who finished are qualified
- If an athlete doesn't finish the first run, `secondRunTime` and `secondRunFinished` are reset to null

## Database Schema

### Core Entities

**Athlete Table:**
- `id`: Primary Key (BIGINT, auto-generated)
- `name`: VARCHAR (NOT NULL) - Athlete's full name
- `country_id`: Foreign Key to Countries table (NOT NULL)
- `gender`: ENUM (NOT NULL) - `MALE` or `FEMALE`
- `date_of_birth`: DATE (NOT NULL)

**Country Table:**
- `id`: Primary Key (BIGINT, auto-generated)
- `name`: VARCHAR (NOT NULL, UNIQUE) - Country name

**Competition Table:**
- `id`: Primary Key (BIGINT, auto-generated)
- `name`: VARCHAR (NOT NULL) - Competition name
- `type`: ENUM (NOT NULL) - `BIATHLON` or `SKI_SLALOM`
- `gender`: ENUM (NOT NULL) - `MALE` or `FEMALE`
- `minimum_age`: INTEGER (NOT NULL)
- `number_of_laps`: INTEGER (nullable, used for BIATHLON only)
- `shooting_after_laps`: INTEGER (nullable, used for BIATHLON only)

**CompetitionRegistration Table:**
- `id`: Primary Key (BIGINT, auto-generated)
- `athlete_id`: Foreign Key to Athletes table (NOT NULL)
- `competition_id`: Foreign Key to Competitions table (NOT NULL)
- Represents athlete enrollment in a specific competition

**BiathlonResult Table:**
- `id`: Primary Key (BIGINT, auto-generated)
- `registration_id`: Foreign Key to CompetitionRegistration table (NOT NULL)
- `ski_time`: DOUBLE (NOT NULL) - Time taken to complete skiing
- `misses`: INTEGER (NOT NULL) - Number of shooting misses
- `penalty_per_miss`: DOUBLE (NOT NULL) - Penalty time per miss
- `finished`: BOOLEAN (NOT NULL) - Whether athlete finished the competition

**SlalomResult Table:**
- `id`: Primary Key (BIGINT, auto-generated)
- `registration_id`: Foreign Key to CompetitionRegistration table (NOT NULL)
- `first_run_time`: DOUBLE (nullable) - Time for first run
- `first_run_finished`: BOOLEAN - Whether first run was completed
- `second_run_time`: DOUBLE (nullable) - Time for second run
- `second_run_finished`: BOOLEAN - Whether second run was completed

### Database Features
- JPA/Hibernate ORM with automatic schema updates (`hibernate.ddl-auto=update`)
- Formatted SQL logging for development
- PostgreSQL-specific optimizations
- Cascading deletes and lazy loading for performance
- Foreign key constraints for referential integrity

## Example API Requests and Responses

### Create a Country
**Request:**
```bash
POST /api/countries
Content-Type: application/json

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

### Create an Athlete
**Request:**
```bash
POST /api/athletes
Content-Type: application/json

{
  "name": "Birgit Skarstein",
  "countryId": 1,
  "gender": "FEMALE",
  "dateOfBirth": "1995-08-15"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Birgit Skarstein",
  "country": {
    "id": 1,
    "name": "Norway"
  },
  "gender": "FEMALE",
  "dateOfBirth": "1995-08-15"
}
```

### Create a Biathlon Competition
**Request:**
```bash
POST /api/competitions
Content-Type: application/json

{
  "name": "Women's 7.5km Sprint",
  "type": "BIATHLON",
  "gender": "FEMALE",
  "minimumAge": 18,
  "numberOfLaps": 3,
  "shootingAfterLaps": 2
}
```

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

### Register Athlete for Competition
**Request:**
```bash
POST /api/registrations
Content-Type: application/json

{
  "athleteId": 1,
  "competitionId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "athlete": {...},
  "competition": {...}
}
```

### Record Biathlon Result
**Request:**
```bash
POST /api/biathlon-results
Content-Type: application/json

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
  "athleteName": "Birgit Skarstein",
  "competitionName": "Women's 7.5km Sprint",
  "skiTime": 45.30,
  "penaltyTime": 2.0,
  "totalTime": 47.30,
  "finished": true
}
```

### Get Biathlon Ranking
**Request:**
```bash
GET /api/biathlon-results/ranking/1
```

**Response:**
```json
[
  {
    "athleteName": "Birgit Skarstein",
    "competitionName": "Women's 7.5km Sprint",
    "skiTime": 45.30,
    "penaltyTime": 2.0,
    "totalTime": 47.30,
    "finished": true,
    "position": 1
  },
  {
    "athleteName": "Anna Magnusson",
    "competitionName": "Women's 7.5km Sprint",
    "skiTime": 46.50,
    "penaltyTime": 1.0,
    "totalTime": 47.50,
    "finished": true,
    "position": 2
  }
]
```

## Configuration

### Application Properties

The application is configured through `src/main/resources/application.properties`:

```properties
# Application name
spring.application.name=winter-olympics

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5434/winter_olympics
spring.datasource.username=olympics
spring.datasource.password=olympics

# JPA/Hibernate configuration
spring.jpa.hibernate.ddl-auto=update        # Auto-update schema on startup
spring.jpa.show-sql=true                    # Log SQL queries
spring.jpa.properties.hibernate.format_sql=true  # Format SQL output

# Server configuration
server.port=8080
```

### Customizing Configuration

To modify configuration:
1. Edit `src/main/resources/application.properties`
2. Restart the application
3. For Docker-based PostgreSQL, ensure credentials match `docker-compose.yml`

## Development

### Building from Source

1. **Clean build**:
   ```bash
   ./mvnw clean install
   ```

2. **Run with hot reload** (using Spring DevTools):
   ```bash
   ./mvnw spring-boot:run
   ```
   Any file changes will trigger automatic restart.

### IDE Setup

- **IntelliJ IDEA**: Open project as Maven project
- **Eclipse**: Import as existing Maven project
- **VS Code**: Install Extension Pack for Java

### Lombok Configuration

This project uses Lombok to reduce boilerplate code. Make sure your IDE recognizes Lombok annotations:
- IntelliJ IDEA: Annotate Processing will automatically enable
- Eclipse: Run `java -jar lombok.jar` in IDE directory

## Testing

### Running Tests

```bash
./mvnw test
```

### Test Coverage

```bash
./mvnw clean test jacoco:report
```

Test files are located in `src/test/java/com/example/winter_olympics/`

## Monitoring and Health Checks

The application includes Spring Boot Actuator endpoints:

- **Health Check**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Tracing**: Distributed tracing enabled via Micrometer

## Security

This application includes Spring Security integration. Default endpoints may require authentication. Customize security settings in:
- `src/main/java/com/example/winter_olympics/config/SecurityConfig.java`

## HTTP Status Codes

The API uses standard HTTP status codes:

- **200 OK**: Successful GET request
- **201 Created**: Successful POST request creating a new resource
- **204 No Content**: Successful DELETE request
- **400 Bad Request**: Invalid input data or validation failure
- **404 Not Found**: Resource not found
- **409 Conflict**: Business logic constraint violation (e.g., athlete not eligible for competition)
- **500 Internal Server Error**: Server-side error

## Error Handling

Global exception handling is configured in:
- `src/main/java/com/example/winter_olympics/config/GlobalExceptionHandler.java`

Common error responses include:
- **404 Not Found**: When resource (Athlete, Country, Competition, etc.) doesn't exist
- **400 Bad Request**: When validation fails or business logic constraints are violated
- **409 Conflict**: When registration violates eligibility criteria (age, gender mismatch)

Example error response:
```json
{
  "message": "Athlete gender does not match competition gender",
  "status": 409,
  "timestamp": "2026-10-07T10:30:00Z"
}
```

## Business Logic and Validation

### Athlete Registration Validation
When registering an athlete for a competition, the following checks are performed:
1. **Gender Match**: Athlete's gender must exactly match the competition's gender requirement
2. **Minimum Age**: Athlete must meet or exceed the competition's minimum age requirement
   - Age is calculated from the athlete's date of birth to the current date

### Competition Configuration Validation
- **BIATHLON**: Requires both `numberOfLaps` and `shootingAfterLaps`
  - `shootingAfterLaps` must be ≤ `numberOfLaps`
  - Both values must be positive integers
- **SKI_SLALOM**: Must NOT have `numberOfLaps` or `shootingAfterLaps` set
  - These fields must be null for slalom competitions

### Biathlon Results Calculation
- **Penalty Time**: `misses × penaltyPerMiss`
- **Total Time**: `skiTime + penaltyTime`
- **Ranking**: Sorted by finished status first, then by total time

### Slalom Results Logic
- Athletes complete two runs
- **Qualification for Second Run**:
  - Only athletes who finished the first run are eligible
  - Only top 50% of finished athletes advance to second run
  - Second run start order is reverse of first run ranking
- **Final Ranking**: Based on combined time from both runs (or disqualified if didn't finish both)

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running: `docker-compose ps`
- Check credentials in `application.properties`
- Ensure database port 5434 is not in use

### Port Already in Use
- Change `server.port` in `application.properties`
- Or kill process on port 8080: `lsof -ti:8080 | xargs kill -9`

### Build Failures
- Clear Maven cache: `./mvnw clean`
- Rebuild: `./mvnw install`
- Check Java version: `java -version` (requires Java 21+)

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/4.0.8/reference/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Docker Documentation](https://docs.docker.com/)

## License

This project is part of a university assignment.

## Support

For issues or questions, refer to the Spring Boot and Spring Data JPA documentation or consult the project maintainers.

