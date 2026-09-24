# AGENTS.md

Java 21 + Spring Boot 4 REST API, single Maven module, package root `dev.fmhj97.whatdoicookbackend`. No CI, no linter/formatter config — `mvn test` is the only verification step.

## Commands
- Full suite: `./mvnw test` (or `mvn test`). Single test: `./mvnw test -Dtest=AuthServiceTest` (`-Dtest=RecipeStepServiceTest`, etc.).
- Run locally: `mvn spring-boot:run`, or `docker-compose up --build` with a root `.env` file. Swagger at `http://localhost:8080/swagger-ui/index.html` (default profile only — see below).
- `docker-compose.yml` runs the app with `SPRING_PROFILES_ACTIVE=prod`, which sets `swagger.enabled=false` (Swagger denied) and `spring.jpa.show-sql=false`. To get Swagger/SQL logs, run `mvn spring-boot:run` instead (default profile has `show-sql=true`).
- Windows/Git Bash: `./mvnw` needs `JAVA_HOME` pointing at a JDK 21.

## Required environment variables
All config comes from env vars (`application.properties`) — there are no working defaults except `CORS_ALLOWED_ORIGINS` (default `http://localhost:5173`). The app will not start, and `WhatDoICookBackendApplicationTests` (a `@SpringBootTest` context-load test) will fail, without:

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION`, `ADMIN_USERNAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`

- `JWT_SECRET` MUST be **Base64-encoded**: `JwtService` does `Decoders.BASE64.decode(...)` then `hmacShaKeyFor(...)`. A non-Base64 or too-short secret throws at token generation, not at startup.
- H2 is test-scoped, but it does NOT rescue `mvn test` — `application.properties` hardcodes `spring.datasource.url=${DB_URL}`, so the context test needs the env vars. The service tests (`src/test/.../service/`) are pure Mockito + AssertJ with no Spring context or DB and run without env vars.
- Docker: compose's postgres is published on host port **5433** (5432 internal); with compose, `DB_URL` should be `jdbc:postgresql://postgres:5432/what_do_i_cook`.

## Database / schema
- `spring.jpa.hibernate.ddl-auto=update` — schema derives from entities. There is NO migration tooling (no Flyway/Liquibase).
- `spring.jpa.open-in-view=false` — all queries must run inside `@Transactional` service methods; don't rely on lazy loading outside a transaction.
- `DataInitializer` (config) runs at startup and is only safe if this holds: creates the ADMIN user from `ADMIN_*` vars when none exists, and seeds a 107-item ingredient catalog when the table is empty.

## Architecture & conventions
- Layered by type: `config/`, `controller/`, `service/`, `repository/`, `entity/`, `dto/<domain>/`, `exception/`, `security/`.
- Entities are never exposed to clients. Controllers return DTO records (with `static from(entity)` factories); request DTOs use `jakarta.validation`.
- Current user is injected via `@AuthenticationPrincipal User` (`User implements UserDetails`).
- Security/ownership is enforced in two places: route-based rules in `SecurityConfig`, and per-resource ownership checks in services that throw `ForbiddenException`. Routes (`SecurityConfig`): `/api/auth/**` and `/api/ping` public (ping used by UpTimeRobot, returns 200 without auth); `/api/recipes/**` = USER only; `/api/ingredients` GET = any authenticated, POST/PATCH/DELETE = ADMIN; `/api/profile` GET = USER or ADMIN, the other `/api/profile/**` (password change, delete) = USER; `/api/admin/**` = ADMIN; anything else `.authenticated()`. Admins have NO access to user recipes.
- All errors return `{"error": "..."}` JSON via `GlobalExceptionHandler` (validation errors return a field→message map); 401/403 are written by `SecurityConfig`.
- Services: constructor injection, `private final` fields, `@Transactional` (readOnly reads).

## Also
- Repo skills available: `java-coding-standards`, `java-springboot`, `java-docs` (under `.agents/skills/`). Load them via the skill tool when writing Java.
- Frontend (Vite) is a separate repo; CORS default targets `http://localhost:5173`.
- Commit style uses conventional prefixes (`feat:`, `fix:`).
- Live deployment runs on Render's free tier — first request after ~15 min idle takes ~30s (see README).