# Student Management System - Project Report Material

## 1. Abstract
The Student Management System is a desktop application that lets a college administrator manage student records and academic marks from one place. It is built with Java Swing for the user interface, JDBC for database access and MySQL for storage, and follows the MVC architecture with the DAO pattern. The administrator logs in securely, adds, updates, deletes and searches students, records subject marks, and views automatically calculated results (total, average, percentage and grade). Passwords are stored as salted hashes, every SQL statement uses prepared statements, and all input is validated before it reaches the database. The project demonstrates core Java, object-oriented design, collections, exception handling, JDBC and GUI development without relying on large frameworks.

## 2. Introduction
Educational institutions maintain large amounts of student data: personal details, course enrolment and examination marks. Handling this on paper or in scattered spreadsheets is slow and error-prone. A database-backed application provides one reliable source of truth with fast search and consistent calculations. This project builds such an application as a standalone desktop program.

## 3. Problem Statement
Manual or spreadsheet-based record keeping causes duplicate entries, inconsistent data, slow searching, calculation mistakes in totals and grades, and no access control. The college needs a simple, secure system that stores student and marks data centrally, validates input, and calculates results automatically.

## 4. Objectives
- Provide secure administrator authentication.
- Store student records in a relational database with integrity constraints.
- Support adding, viewing, searching, updating and deleting students.
- Record subject-wise marks and reject invalid values.
- Calculate total, average, percentage and grade automatically.
- Show a dashboard with summary statistics and quick navigation.
- Demonstrate clean layered design (MVC + DAO) using core Java technologies.

## 5. Scope
**In scope:** one user role (administrator), student records, subject marks (each out of 100), result calculation, searching, a desktop GUI, a single-machine MySQL database.
**Out of scope:** student or teacher logins, attendance, fee management, report-card printing, web or mobile access, multi-user concurrency control beyond what MySQL provides.

## 6. Existing System
Most small institutions rely on paper registers or Excel sheets. Problems: data is duplicated across files, no validation, difficult searching, formulas can be accidentally overwritten, no password protection per user, and no referential integrity between students and marks.

## 7. Proposed System
A Java Swing desktop application connected to MySQL. All data lives in three related tables. Input is validated in the service layer and again by database constraints. Searching uses indexed columns. Results are computed by one grading class so the rule is applied consistently. The administrator logs in with a hashed password, and no credentials are hard-coded in source code.

## 8. Functional Requirements
| ID | Requirement |
|---|---|
| FR-1 | The system shall authenticate an administrator using username and password. |
| FR-2 | The system shall reject empty login fields and wrong credentials with clear messages. |
| FR-3 | The system shall add a student with auto-generated Student ID. |
| FR-4 | The system shall display all students in a table. |
| FR-5 | The system shall search students by ID, name, department or course. |
| FR-6 | The system shall update and delete a selected student. |
| FR-7 | The system shall load a selected table row into the form. |
| FR-8 | The system shall add, update, delete and display marks per subject for a student. |
| FR-9 | The system shall reject marks outside 0-100 and duplicate subjects for a student. |
| FR-10 | The system shall calculate total, subject count, average, percentage and grade. |
| FR-11 | The system shall show total students, departments and courses on a dashboard. |
| FR-12 | The system shall allow the administrator to log out and return to the login screen. |

## 9. Non-functional Requirements
- **Usability:** simple, consistent screens with clear error messages.
- **Security:** salted password hashes, prepared statements, no credentials in source code.
- **Reliability:** invalid input and database failures do not crash the application.
- **Performance:** indexed searches; results calculated with two queries.
- **Maintainability:** layered design, single-responsibility classes, small methods.
- **Portability:** runs on any OS with Java 17+ and MySQL 8.
- **Data integrity:** primary keys, foreign keys, unique and CHECK constraints.

## 10. Hardware Requirements
| Item | Minimum |
|---|---|
| Processor | Dual-core, 1.6 GHz or faster |
| RAM | 4 GB (8 GB recommended with IntelliJ IDEA) |
| Disk | 1 GB free |
| Display | 1280 x 720 or higher |

## 11. Software Requirements
| Item | Version |
|---|---|
| Operating system | Windows 10/11, Linux or macOS |
| JDK | 17 or later |
| Database | MySQL Server 8.0.16 or later |
| JDBC driver | MySQL Connector/J 8.4.0 (via Maven) |
| Build tool | Apache Maven 3.8+ |
| IDE | IntelliJ IDEA or Eclipse |
| Tools | MySQL Workbench (optional) |

## 12. System Architecture
The application uses a layered architecture:

```
+--------------------+
|  View (Swing UI)   |  LoginFrame, DashboardFrame, StudentFrame, MarksFrame, ResultFrame, Navigator
+---------+----------+
          |
+---------v----------+
|  Service layer     |  AuthenticationService, StudentService, MarkService, ResultService
+---------+----------+
          |
+---------v----------+
|  DAO layer         |  AdminDAO, StudentDAO, MarkDAO
+---------+----------+
          |
+---------v----------+
|  DatabaseConnection|  JDBC + application.properties
+---------+----------+
          |
      MySQL 8
```
Models (`Admin`, `Student`, `Mark`, `StudentResult`) flow between layers; utilities (`ValidationUtil`, `GradeUtil`, `PasswordUtil`) provide stateless helpers.
The dependency direction is one-way (UI -> service -> DAO -> database), so each layer can be understood, tested and changed independently.

## 13. ER Diagram Description
**Entities:** Admin, Student, Mark.
- **Admin** (id PK, username UNIQUE, password, salt) - not related to the other entities.
- **Student** (student_id PK, name, email UNIQUE, phone, gender, date_of_birth, department, course, year).
- **Mark** (mark_id PK, student_id FK, subject, marks), with a UNIQUE pair (student_id, subject).

**Relationship:** one Student has zero or many Marks (1 : N). The foreign key `marks.student_id` references `students.student_id` with `ON DELETE CASCADE`, so deleting a student deletes their marks.

```
ADMIN                    STUDENT (1) ------< (N) MARK
 id PK                    student_id PK            mark_id PK
 username                 name                     student_id FK
 password                 email                    subject
 salt                     phone                    marks
                          gender
                          date_of_birth
                          department
                          course
                          year
```

## 14. Use Case Description
**Actor:** Administrator.

| Use case | Description |
|---|---|
| Login | Admin enters username and password; system verifies the hash and opens the dashboard. |
| View dashboard | Shows student, department and course counts and navigation buttons. |
| Manage students | Add, update, delete, clear form, view details. |
| Search students | Choose a field (ID, name, department, course), enter text, table updates. |
| Manage marks | Select student, add/update/delete subject marks, view summary. |
| View results | See total, average, percentage and grade for all students. |
| Logout | Ends the session and returns to the login screen. |

**Main flow - Add student:** Admin opens Students -> fills the form -> clicks Add -> system validates -> checks duplicate email -> inserts -> shows new ID -> table refreshes.
**Alternate flow:** Validation fails -> an error message is shown and nothing is saved.

## 15. Database Design
Database: `student_management` (utf8mb4, InnoDB).

**admins**
| Column | Type | Constraints |
|---|---|---|
| id | INT | PK, AUTO_INCREMENT |
| username | VARCHAR(50) | NOT NULL, UNIQUE |
| password | CHAR(64) | NOT NULL (SHA-256 hex) |
| salt | VARCHAR(32) | NOT NULL |

**students**
| Column | Type | Constraints |
|---|---|---|
| student_id | INT | PK, AUTO_INCREMENT (starts at 1001) |
| name | VARCHAR(100) | NOT NULL, indexed |
| email | VARCHAR(120) | NOT NULL, UNIQUE |
| phone | VARCHAR(10) | NOT NULL, CHECK 10 digits |
| gender | VARCHAR(10) | NOT NULL, CHECK Male/Female/Other |
| date_of_birth | DATE | NOT NULL |
| department | VARCHAR(80) | NOT NULL, indexed |
| course | VARCHAR(80) | NOT NULL, indexed |
| year | TINYINT | NOT NULL, CHECK 1-4 |

**marks**
| Column | Type | Constraints |
|---|---|---|
| mark_id | INT | PK, AUTO_INCREMENT |
| student_id | INT | NOT NULL, FK -> students, ON DELETE CASCADE |
| subject | VARCHAR(80) | NOT NULL |
| marks | DECIMAL(5,2) | NOT NULL, CHECK 0-100 |

Unique key (student_id, subject) prevents duplicate subjects. The tables are in third normal form: every non-key column depends only on the key of its table.

## 16. Module Description
1. **Authentication module** - `LoginFrame`, `AuthenticationService`, `AdminDAO`, `PasswordUtil`.
2. **Dashboard module** - `DashboardFrame`, `Navigator`; counts come from `StudentDAO.getStats()`.
3. **Student module** - `StudentFrame`, `StudentService`, `StudentDAO`, `ValidationUtil`.
4. **Marks module** - `MarksFrame`, `MarkService`, `MarkDAO`.
5. **Result module** - `ResultFrame`, `ResultService`, `GradeUtil`.
6. **Database module** - `DatabaseConnection`, `application.properties`.

## 17. Implementation Details
- **JDBC:** every query uses `PreparedStatement` with `?` parameters inside try-with-resources, so connections, statements and result sets always close. User input is never concatenated into SQL; the only strings joined are fixed SQL constants.
- **Configuration:** `DatabaseConnection` reads `application.properties` from the classpath; environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` override it.
- **Password security:** `PasswordUtil` computes SHA-256(salt + password) with `java.security.MessageDigest`; comparison uses `MessageDigest.isEqual` (constant time). MySQL's `SHA2()` produces the same value, so the SQL script can seed the admin.
- **Validation:** `ValidationUtil` holds regex and range checks; services decide which message to show. The database CHECK constraints are a second line of defence.
- **Error handling:** DAOs throw `SQLException`; services translate it through `DatabaseErrors` into a `ServiceException` with a friendly message (connection failure, wrong credentials, duplicate key, missing database). The full exception is logged to the console only.
- **Grading:** `GradeUtil.calculateGrade(percentage)`: 90+ A+, 80+ A, 70+ B, 60+ C, 50+ D, otherwise F. Because every subject is out of 100, percentage = total / (subjects x 100) x 100, which equals the average.
- **Swing:** `JFrame`, `JPanel`, `JTable` with `DefaultTableModel`, `JComboBox`, layout managers (`BorderLayout`, `GridBagLayout`, `GridLayout`, `FlowLayout`), `JOptionPane`. Table row selection loads the form via a `ListSelectionListener`; table sorting is enabled and row indexes are converted with `convertRowIndexToModel`.
- **Collections / Java features:** `List`, `Map` with `Collectors.groupingBy`, `Optional`, records, switch expressions, streams.

## 18. Testing
Unit tests (JUnit 5) cover grading boundaries, validation rules, password hashing and result calculation. Manual test cases (33 cases) cover login, CRUD, search, validation, marks, results, navigation and database failure. See `docs/TESTING.md`.

## 19. Advantages
- Central, consistent data storage with integrity constraints.
- Fast, flexible searching.
- Automatic result calculation removes manual errors.
- Secure: hashed passwords, prepared statements, no hard-coded credentials.
- Clean layered code that is easy to extend and explain.
- Runs offline on one computer; no framework or server needed.

## 20. Limitations
- Single role (administrator only); no student or teacher login.
- Salted SHA-256 is fast; production systems should use BCrypt/PBKDF2/Argon2.
- All subjects are assumed to be out of 100 with equal weight.
- Desktop only; no network or web access.
- Database calls run on the UI thread (fine for small data, would need `SwingWorker` for large data).
- No report export (PDF/Excel) or audit log.

## 21. Future Enhancements
- Role-based access (admin, teacher, student) and password change screen.
- BCrypt or Argon2 password hashing.
- Export results to PDF/Excel; print report cards.
- Attendance, fee and timetable modules.
- Semester-wise marks, credits and CGPA/GPA.
- Pagination and background loading with `SwingWorker`.
- Web version using Spring Boot and a REST API.
- Photo upload and email notifications.

## 22. Conclusion
The Student Management System meets its objectives: it provides secure login, complete student and marks management, automatic result calculation and a clear dashboard, built on a clean MVC + DAO design using only core Java technologies. The project demonstrates object-oriented principles, JDBC with prepared statements, relational database design, input validation and exception handling, and forms a solid base for future extensions.
