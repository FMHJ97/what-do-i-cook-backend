# What Do I Cook? — Backend

**What Do I Cook?** is a REST API built with Spring Boot that helps users decide what to cook based on the ingredients they have at home. Users can manage their own recipes with ingredients and steps, filter by food type, and get a random recipe suggestion.

This project was built as a personal project to learn and practice Spring Boot, Spring Security with JWT, JPA, and PostgreSQL — and to have something real to show.

---

## ✨ Features

### Users
- Register and log in with JWT authentication.
- Full CRUD for their own recipes (title, description, food type, servings, prep/cook time).
- Add ingredients and steps to each recipe.
- Filter their recipes by title, food type, or ingredients they have at home.
- Get a random recipe suggestion (optionally filtered by food type).
- View full recipe details including ingredients and steps in a single request.

### Admins
- Full CRUD for the ingredient catalog.
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

Full API documentation available at `/swagger-ui.html` when running locally.

---

## 🚀 Running Locally

### Requirements

- Java 21+
- Maven
- PostgreSQL

### Steps

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
   JWT_SECRET=your_base64_secret
   JWT_EXPIRATION=86400000
   ADMIN_USERNAME=admin
   ADMIN_EMAIL=admin@example.com
   ADMIN_PASSWORD=your_password
   DB_URL=jdbc:postgresql://localhost:5432/what_do_i_cook
   DB_USERNAME=your_db_user
   DB_PASSWORD=your_db_password
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the Swagger UI at:
   ```
   http://localhost:8080/swagger-ui.html
   ```

On startup, the app automatically creates the default admin user and seeds the ingredient catalog (107 ingredients).

---

## 🔐 Authentication

All protected endpoints require a `Bearer` token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

Get your token by calling `/api/auth/login` or `/api/auth/register`.

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
