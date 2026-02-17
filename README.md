Secure Docs — Spring Boot Backend
REST API built with Spring Boot 3 and Java 21, secured via Keycloak OIDC and deployed as a Docker container.

Implementation Details

1. Bilingual Error Handling
The API responds in Arabic or English based on the client's Accept-Language header.
A @ControllerAdvice intercepts all exceptions and returns a consistent JSON error structure with a localized message.


json// Accept-Language: ar
{ "status": 404, "message": "المورد غير موجود" }

// Accept-Language: en
{ "status": 404, "message": "Resource not found" }


2. Role-Based Access Control
Integrated with Keycloak (OIDC) for identity management.
JWT tokens are validated on every secured request.


3. Database Schema Management
All schema changes are managed via Liquibase changelogs,
ensuring consistent and reproducible database states across environments.


Tech Stack
Java 21 · Spring Boot 3.2 · Spring Security 6 · PostgreSQL 15 · Liquibase · Docker

Deployment
Managed via [secure-docs-deploy](https://github.com/Salamndir/secure-docs-deploy)
