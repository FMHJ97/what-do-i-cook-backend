# What Do I Cook? — Backend

**What Do I Cook?** is a REST API built with Spring Boot that helps users decide what to cook based on the ingredients they have at home. Users can manage their own recipes with ingredients and steps, filter by food type, and get a random recipe suggestion.

This project was built as a personal project to learn and practice Spring Boot, Spring Security with JWT, JPA, and PostgreSQL — and to have something real to show.

---

## 🌐 Live Demo

- **API Base URL:** https://what-do-i-cook-backend.onrender.com
- **Swagger UI:** https://what-do-i-cook-backend.onrender.com/swagger-ui/index.html

> The service runs on Render's free tier. If it hasn't received any requests in the last 15 minutes, the first request may take around 30 seconds to respond.

---

## ✨ Features

### Users
- Register and log in with JWT authentication.
- Full CRUD for their own recipes (title, description, food type, servings, prep/cook time).
- Add ingredients and steps to each recipe.
- Filter their recipes by title, food type, or ingredients they have at home.
- Get a random recipe suggestion (optionally filtered by food type).
- View full recipe details including ingredients and steps in a single request.
- view their own profile info.

### Admins
- Full CRUD for the ingredient catalog.
- View and delete user accounts.
- No access to user recipes — full privacy.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security + JWT (jjwt 0.13.0) |
| Persistence | Spring Data JPA + Hibernate 7 |
| Database | PostgreSQL |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| Containerization | Docker + Docker Compose |
| Testing | JUnit 5 + Mockito |

---

## 📐 Architecture

The project follows a standard layered architecture:

```
Controller → Service → Repository → Database
```

- **DTOs** for all input and output — entities are never exposed directly.
- **Global exception handling** with consistent JSON error responses.
- **Role-based access control** — `USER` and `ADMIN` roles with different permissions.
- **JWT stateless authentication** — no sessions.

---

## 🗂 Main Endpoints

| Method | Endpoint | Description | Role |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | Public |
| POST | `/api/auth/login` | Log in and get a JWT token | Public |
| GET | `/api/recipes` | List your recipes (filterable) | USER |
| GET | `/api/recipes/{id}/details` | Full recipe details with ingredients and steps | USER |
| GET | `/api/recipes/random` | Get a random recipe | USER |
| GET | `/api/recipes/filter` | Filter recipes by ingredients you have | USER |
| POST | `/api/recipes` | Create a recipe | USER |
| PATCH | `/api/recipes/{id}` | Update a recipe | USER |
| DELETE | `/api/recipes/{id}` | Delete a recipe | USER |
| GET/POST/PATCH/DELETE | `/api/recipes/{id}/steps` | Manage recipe steps | USER |
| GET/POST/PATCH/DELETE | `/api/recipes/{id}/ingredients` | Manage recipe ingredients | USER |
| GET | `/api/ingredients` | Browse ingredient catalog | USER + ADMIN |
| POST/PATCH/DELETE | `/api/ingredients` | Manage ingredient catalog | ADMIN |
| GET | `/api/profile` | View your own profile | USER |
| PATCH | `/api/profile/password` | Change your own password | USER |
| DELETE | `/api/profile` | Delete your own account | USER |
| GET | `/api/admin/users` | List all users (excluding admins) | ADMIN |
| GET | `/api/admin/users/{id}` | Get a user by ID | ADMIN |
| DELETE | `/api/admin/users/{id}` | Delete a user by ID | ADMIN |

Full API documentation available at `/swagger-ui/index.html`.

---

## 🚀 Running Locally

### Option A — Maven (requires Java 21+ and PostgreSQL)

1. Clone the repository:
   ```bash
   git clone https://github.com/FMHJ97/what-do-i-cook-backend.git
   cd what-do-i-cook-backend
   ```

2. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE "what_do_i_cook";
   ```

3. Set the following environment variables (in your IDE or system):

   ```
   DB_URL=jdbc:postgresql://localhost:5432/what_do_i_cook
   DB_USERNAME=your_db_user
   DB_PASSWORD=your_db_password
   JWT_SECRET=your_base64_secret
   JWT_EXPIRATION=86400000
   ADMIN_USERNAME=admin
   ADMIN_EMAIL=admin@example.com
   ADMIN_PASSWORD=your_password
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

---

### Option B — Docker Compose (Docker Desktop required)

1. Clone the repository:
   ```bash
   git clone https://github.com/FMHJ97/what-do-i-cook-backend.git
   cd what-do-i-cook-backend
   ```

2. Create a `.env` file in the root of the project:

   ```
   DB_URL=jdbc:postgresql://postgres:5432/what_do_i_cook
   DB_USERNAME=your_db_user
   DB_PASSWORD=your_db_password
   JWT_SECRET=your_base64_secret
   JWT_EXPIRATION=86400000
   ADMIN_USERNAME=admin
   ADMIN_EMAIL=admin@example.com
   ADMIN_PASSWORD=your_password
   ```

3. Start the app and database together:
   ```bash
   docker-compose up --build
   ```

4. Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

On startup, the app automatically creates the default admin user and seeds the ingredient catalog (107 ingredients).

---

## 🔐 Authentication

All protected endpoints require a `Bearer` token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

Get your token by calling `/api/auth/login` or `/api/auth/register`.

---

## 🧪 Testing

The project includes unit tests for all service classes using JUnit 5 and Mockito. Dependencies are fully mocked — no database or Spring context is loaded.

Tests cover:

- `AuthService` — registration, login, duplicate checks, token generation, `lastLoginAt` update.
- `RecipeService` — CRUD, ownership checks, filtering by title, food type and ingredients.
- `RecipeStepService` — CRUD, ownership checks, automatic step renumbering on delete.
- `IngredientService` — CRUD, case-insensitive name filtering, duplicate checks.
- `UserService` — profile info, password change, account deletion with password verification, admin user management.

Run the tests with:

```bash
mvn test
```

---

## 📁 Project Structure

```
src/main/java/dev/fmhj97/whatdoicookbackend/
├── config/          # App configuration (Swagger, DataInitializer)
├── controller/      # REST controllers
├── dto/             # Request and response DTOs
├── entity/          # JPA entities and enums
├── exception/       # Custom exceptions and global handler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, service and Spring Security config
└── service/         # Business logic
```
