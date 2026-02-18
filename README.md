# User CRUD API

A RESTful API built with Spring Boot for managing users, including profile photo upload and retrieval.

## Features

- Create and persist users with name and birth date
- Search users by name or ID
- Delete users by ID
- Upload and download user profile photos (stored on the file system as JPEG)
- Photo URI automatically generated and exposed in user responses

## Endpoints

| Method | Path                        | Description                  |
|--------|-----------------------------|------------------------------|
| PUT    | /api/user/save              | Create a new user            |
| GET    | /api/user/find?name={name}  | Find users by name           |
| GET    | /api/user/find/{id}         | Find user by ID              |
| POST   | /api/user/delete/{id}       | Delete user by ID            |
| POST   | /api/user/photo?id={id}     | Upload user profile photo    |
| GET    | /api/user/photo/{photoUUID} | Download user profile photo  |

## Technologies

| Technology        | Version | Purpose                                      |
|-------------------|---------|----------------------------------------------|
| Java              | 11      | Programming language                         |
| Spring Boot       | 2.7.3   | Application framework                        |
| Spring Web MVC    | -       | REST API layer                               |
| Spring Data JPA   | -       | Database abstraction and repositories        |
| Hibernate         | -       | ORM / PostgreSQL dialect                     |
| PostgreSQL        | -       | Relational database                          |
| Lombok            | -       | Boilerplate code reduction (getters/setters) |
| Maven             | 3.8.6   | Build and dependency management              |

## Prerequisites

- Java 11+
- Maven 3.8+
- PostgreSQL running on `localhost:5432`
  - Database: `postgres`
  - Username: `postgres`
  - Password: `postgre`

## Database Setup

The application expects a `users` table with the following columns:

```sql
CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    birth_date DATE,
    photo_uuid UUID
);
```

## Running the Application

```sh
cd user
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080/api`

## Configuration

Key properties defined in [`application.properties`](user/src/main/resources/application.properties):

| Property                              | Description                         |
|---------------------------------------|-------------------------------------|
| `users.photos.path`                   | Local directory to store photos     |
| `spring.datasource.url`               | PostgreSQL JDBC URL                 |
| `spring.servlet.multipart.max-file-size` | Max upload file size (10MB)      |
| `server.servlet.context-path`         | Base path for all endpoints (`/api`)|