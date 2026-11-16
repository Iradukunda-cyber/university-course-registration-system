# University Course Registration and Academic Management System

Spring Boot 4 / Java 17 project implementing the Model and Repository layers with Spring Data JPA, Hibernate, and PostgreSQL.

## Database setup

Create the PostgreSQL database:

```sql
CREATE DATABASE university_academic_db;
```

The application connects to `localhost:5432/university_academic_db`. Credentials are read from environment variables so secrets are not stored in the repository:

```powershell
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "<your PostgreSQL password>"
```

Set `DB_USERNAME` and `DB_PASSWORD` in the environment used to launch the application. The username defaults to `postgres`; the password defaults to an empty string. Hibernate's `update` mode creates or updates the mapped tables when the application connects.

## Build and verification

Run the mapping tests (they use an isolated in-memory H2 database and do not require PostgreSQL):

```powershell
.\mvnw.cmd test
```

Launch against PostgreSQL after creating the database and setting credentials:

```powershell
.\mvnw.cmd spring-boot:run
```

The design rationale, ERD, and relational schema are in [ANALYSIS_AND_DESIGN.md](ANALYSIS_AND_DESIGN.md), [ERD.md](ERD.md), and [SCHEMA.md](SCHEMA.md). For submission screenshots, use a PostgreSQL client such as pgAdmin or `psql` to show the generated tables, foreign keys, and constraints after launching the application.
