# Student Management System

A desktop application for managing student records and academic marks, built with **core Java** (no Spring, no Hibernate).

**Stack:** Java 17+ · Swing · JDBC · MySQL 8 · Maven · MVC + DAO pattern

## Features
- Admin login with salted SHA-256 password hashing
- Dashboard with total students, departments and courses
- Student CRUD with search by ID, name, department or course
- Marks management (add / update / delete per subject, range-validated 0-100)
- Result calculation: total, number of subjects, average, percentage, grade
- Friendly validation and error messages (no stack traces shown to users)

## Architecture
```
ui  ->  service  ->  dao  ->  database (JDBC)  ->  MySQL
```
| Package | Responsibility |
|---|---|
| `model` | Plain data classes: `Admin`, `Student`, `Mark`, `StudentResult`, ... |
| `dao` | All SQL, via `PreparedStatement` and try-with-resources |
| `service` | Business rules: login, validation, duplicate checks, result calculation |
| `ui` | Swing screens + `Navigator` (screen switching) |
| `database` | `DatabaseConnection`, reads `application.properties` |
| `util` | `ValidationUtil`, `GradeUtil`, `PasswordUtil` |

## Setup

### 1. Requirements
JDK 17+, MySQL Server 8.0.16+ (needed for `CHECK` constraints), Maven (bundled in IntelliJ IDEA).

### 2. Create the database
```bash
mysql -u root -p < sql/student_management.sql
```
Or open `sql/student_management.sql` in MySQL Workbench and run it. **The script drops and recreates the `student_management` database.**

### 3. Configure the connection
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Edit `application.properties` and set `db.username` and `db.password`.
`application.properties` is in `.gitignore`, so your password is never pushed to GitHub.
Alternatively set the environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (they override the file).

### 4. Run
- **IntelliJ IDEA:** open the folder as a Maven project, wait for dependencies to download, run `com.studentmanagement.Main`.
- **Terminal:** `mvn compile exec:java`
- **Runnable JAR:** `mvn clean package` then `java -jar target/student-management-system-1.0.0.jar`

### Default login
| Username | Password |
|---|---|
| `admin` | `Admin@123` |

## Adding another admin
Passwords are stored as `SHA-256(salt + password)`. In MySQL:
```sql
SET @salt = 'pick_a_random_salt';
INSERT INTO admins (username, password, salt)
VALUES ('teacher1', SHA2(CONCAT(@salt, 'MyPassword1'), 256), @salt);
```

## Tests
`mvn test` runs the JUnit 5 tests (grading, validation, password hashing, result calculation). They need no database.
Manual test cases are in [docs/TESTING.md](docs/TESTING.md).

## Documentation
- [docs/PROJECT_REPORT.md](docs/PROJECT_REPORT.md) - material for the college project report
- [docs/VIVA_QUESTIONS.md](docs/VIVA_QUESTIONS.md) - likely viva questions and answers
- [docs/TESTING.md](docs/TESTING.md) - test cases, sample data, common errors

## Security note
SHA-256 with a per-user salt avoids plain-text storage and needs no extra library, but it is fast to compute. A production system should use a deliberately slow algorithm such as BCrypt, PBKDF2 or Argon2.
