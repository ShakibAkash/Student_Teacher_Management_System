# Student Management System - Setup Guide

## Prerequisites

- **Java 17** (JDK)
- **Maven 3.8+**
- **Docker & Docker Compose** (for containerized setup)
- **PostgreSQL 16** (if running locally without Docker)

---

## Option 1: Run with Docker (Recommended)

This is the easiest way to get started. Docker will handle both the database and the application.

```bash
# Clone the repository
git clone <your-repo-url>
cd SMS

# Build and start all services
docker-compose up --build

# The app will be available at:
# http://localhost:8080
```

To stop:

```bash
docker-compose down
```

To stop and remove data volumes:

```bash
docker-compose down -v
```

---

## Option 2: Run Locally (without Docker)

### Step 1: Set up PostgreSQL

Install PostgreSQL and create the database:

```sql
CREATE USER sms_user WITH PASSWORD 'sms_password';
CREATE DATABASE sms_db OWNER sms_user;
GRANT ALL PRIVILEGES ON DATABASE sms_db TO sms_user;
```

Or using `psql`:

```bash
psql -U postgres
# Then run the SQL commands above
```

### Step 2: Configure the Application

The default configuration in `src/main/resources/application.properties` connects to:

- **Host:** localhost:5432
- **Database:** sms_db
- **Username:** sms_user
- **Password:** sms_password

Modify these if your PostgreSQL setup differs.

### Step 3: Build and Run

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/student-management-system-1.0.0.jar
```

The app will start at: **http://localhost:8080**

---

## Default Accounts

On first startup, the app seeds the following accounts:

| Username | Password | Role    |
| -------- | -------- | ------- |
| teacher1 | password | TEACHER |
| student1 | password | STUDENT |

You can also register new accounts via the **Sign Up** page.

---

## Running Tests

### Unit Tests Only

```bash
mvn test -Dspring.profiles.active=test
```

### All Tests (Unit + Integration)

```bash
mvn verify -Dspring.profiles.active=test
```

The test profile uses an **H2 in-memory database**, so no PostgreSQL is required for testing.

---

## Project Structure

```
SMS/
├── src/
│   ├── main/
│   │   ├── java/com/sms/
│   │   │   ├── config/          # Security configuration
│   │   │   ├── controller/      # Web controllers
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Spring Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── SmsApplication.java
│   │   └── resources/
│   │       ├── static/css/      # Stylesheets
│   │       ├── templates/       # Thymeleaf HTML templates
│   │       └── application.properties
│   └── test/
│       ├── java/com/sms/
│       │   ├── integration/     # Integration tests
│       │   └── service/         # Unit tests
│       └── resources/
│           └── application-test.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .gitignore
├── .github/workflows/ci.yml
└── setup.md
```

---

## CI/CD

The project includes a GitHub Actions workflow (`.github/workflows/ci.yml`) that:

1. Runs unit tests with H2 database
2. Runs integration tests with a PostgreSQL service container
3. Builds the Maven package
4. Builds the Docker image

---

## Authorization Summary

### Student Role

| Section     | View | Edit |
| ----------- | ---- | ---- |
| Courses     | ✅   | ❌   |
| Departments | ✅   | ❌   |
| Teachers    | ✅   | ❌   |
| My Profile  | ✅   | ✅   |

### Teacher Role

| Section     | View | Edit |
| ----------- | ---- | ---- |
| Students    | ✅   | ✅   |
| Courses     | ✅   | ✅   |
| Departments | ✅   | ❌   |
| Teachers    | ✅   | ❌   |
| My Profile  | ✅   | ✅   |
