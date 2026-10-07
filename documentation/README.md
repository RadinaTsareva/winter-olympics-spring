# Documentation

This folder contains comprehensive technical documentation for the Winter Olympics Backend API.

## Files

### [TECHNICAL_SPECIFICATION.md](./TECHNICAL_SPECIFICATION.md)

Complete technical specification covering:

- **Architecture** - Layered architecture and component overview
- **Authentication & Authorization** - JWT-based security, roles, and permissions
- **API Endpoints** - All endpoints with request/response examples:
  - Public endpoints (no auth required)
  - Authenticated endpoints (ATHLETE, ADMIN)
  - Admin-only endpoints
- **Database Schema** - Complete database design with relationships
- **Data Transfer Objects (DTOs)** - All request/response data structures
- **Business Logic & Validation** - Core business rules and constraints
- **Error Handling** - Error scenarios and HTTP status codes
- **Configuration** - Application properties and setup
- **Integration Notes** - Frontend integration guidelines

## Quick Links

### For Frontend Developers
- [API Endpoints](./TECHNICAL_SPECIFICATION.md#api-endpoints)
- [DTOs and Data Structures](./TECHNICAL_SPECIFICATION.md#data-transfer-objects-dtos)
- [Authentication Flow](./TECHNICAL_SPECIFICATION.md#jwt-authentication-flow)
- [Integration Notes](./TECHNICAL_SPECIFICATION.md#integration-notes-for-frontend)

### For Backend Developers
- [Architecture](./TECHNICAL_SPECIFICATION.md#architecture)
- [Database Schema](./TECHNICAL_SPECIFICATION.md#database-schema)
- [Business Logic](./TECHNICAL_SPECIFICATION.md#business-logic--validation)
- [Configuration](./TECHNICAL_SPECIFICATION.md#configuration)

### For DevOps/Infrastructure
- [Technology Stack](../README.md#technology-stack)
- [Docker Setup](../README.md#quick-start)
- [Configuration](./TECHNICAL_SPECIFICATION.md#configuration)

## Backend Overview

**Service Name:** Winter Olympics Management System - Backend

**Purpose:** REST API for managing Olympic competitions, athletes, results, and rankings

**Key Endpoints:**
- Authentication: `/api/auth/**`
- Public Data: `/api/olympics/**`
- Athlete Management: `/api/athletes/**`
- Competition Management: `/api/competitions/**`
- Results & Rankings: `/api/biathlon-results/**`, `/api/slalom-results/**`

**Key Features:**
- JWT-based authentication
- Role-based access control (ADMIN, ATHLETE)
- Automatic ranking calculations
- Medal tracking and statistics
- Public API for viewing results

## Database

**Type:** PostgreSQL 17

**Main Entities:**
- Users (with roles)
- Athletes
- Countries
- Competitions (Biathlon, Ski Slalom)
- Competition Registrations
- Biathlon Results
- Slalom Results

For detailed schema, see [TECHNICAL_SPECIFICATION.md#database-schema](./TECHNICAL_SPECIFICATION.md#database-schema)

## Development

**Language:** Java 21
**Framework:** Spring Boot 4.0.8
**Build Tool:** Maven
**Deployment:** Docker

For setup instructions, see [README.md](../README.md#quick-start)

## API Versioning

Currently using API version v1 (implicit in `/api/` endpoints).

Future versions would use `/api/v2/`, `/api/v3/`, etc.

## Support & Contact

For questions about this documentation:
- Check the relevant section in [TECHNICAL_SPECIFICATION.md](./TECHNICAL_SPECIFICATION.md)
- Consult Spring Boot documentation: https://docs.spring.io/spring-boot/
- Review the main [README.md](../README.md)

