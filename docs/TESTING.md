# Testing Guide

## Sample login
| Username | Password | Expected |
|---|---|---|
| `admin` | `Admin@123` | Dashboard opens |
| `admin` | `wrong` | "Incorrect username or password." |
| (empty) | (any) | "Username is required." |
| `admin` | (empty) | "Password is required." |

## Sample data (loaded by the SQL script)

| ID | Name | Department | Course | Year | Marks | Total | Avg % | Grade |
|---|---|---|---|---|---|---|---|---|
| 1001 | Aarav Sharma | Computer Science | B.Tech CSE | 3 | 95, 88, 92, 85 | 360 | 90.00 | A+ |
| 1002 | Priya Reddy | Computer Science | B.Tech CSE | 2 | 82, 78, 85 | 245 | 81.67 | A |
| 1003 | Rohan Verma | Electronics | B.Tech ECE | 4 | 65, 70, 58 | 193 | 64.33 | C |
| 1004 | Sneha Iyer | Electronics | B.Tech ECE | 3 | 74, 71, 79 | 224 | 74.67 | B |
| 1005 | Kiran Kumar | Mechanical | B.Tech Mechanical | 1 | 45, 52, 38 | 135 | 45.00 | F |
| 1006 | Ananya Das | Business Admin | BBA | 2 | 55, 60 | 115 | 57.50 | D |
| 1007 | Vikram Singh | Business Admin | MBA | 1 | (none) | - | - | N/A |
| 1008 | Meera Nair | Computer Science | BCA | 2 | (none) | - | - | N/A |

Dashboard with the sample data: **8 students, 4 departments, 6 courses**.
(Departments: Computer Science, Electronics, Mechanical, Business Admin. Courses: B.Tech CSE, B.Tech ECE, B.Tech Mechanical, BBA, MBA, BCA.)

## Manual test cases

| ID | Area | Steps | Expected result |
|---|---|---|---|
| T-01 | Login | Login with `admin` / `Admin@123` | Dashboard opens showing "Logged in as: admin" |
| T-02 | Dashboard | Open dashboard with sample data | Students = 8, Departments = 4, Courses = 6 |
| T-03 | Logout | Click Logout, confirm Yes | Login screen appears; Back button cannot reopen dashboard |
| T-04 | Add student | Fill all fields with valid data, click Add | Success message with new ID (1009); row appears in table |
| T-05 | Required fields | Click Add with empty form | "Full name is required." |
| T-06 | Email format | Email `abc@` | "Enter a valid email address..." |
| T-07 | Duplicate email | Add student using `aarav.sharma@example.com` | "Another student already uses this email address." |
| T-08 | Phone | Phone `12345` | "Phone number must be 10 digits and start with 6, 7, 8 or 9." |
| T-09 | DOB format | DOB `15/03/2004` | "Date of birth must be a real date in YYYY-MM-DD format." |
| T-10 | DOB future | DOB `2099-01-01` | "Date of birth must be between 1900-01-01 and today." |
| T-11 | Select row | Click a row in the table | All form fields fill with that student's data |
| T-12 | Update | Select 1002, change course, click Update | "Student updated successfully."; table shows new course |
| T-13 | Update w/o selection | Click Update with nothing selected | "Select a student from the table first." |
| T-14 | Delete | Select 1008, Delete, confirm Yes | Student removed; table refreshes |
| T-15 | Delete cascade | Delete 1006, then open Results | 1006 gone; no orphan marks (FK `ON DELETE CASCADE`) |
| T-16 | Search by ID | Search by Student ID = `1003` | Only Rohan Verma shown |
| T-17 | Search by name | Search Name = `sha` | Aarav Sharma shown (partial, case-insensitive) |
| T-18 | Search dept | Search Department = `electr` | Rohan and Sneha shown |
| T-19 | Search course | Search Course = `BBA` | Ananya Das shown |
| T-20 | Empty search | Click Search with empty box | "Enter something to search for." |
| T-21 | Invalid ID | Student ID search = `abc` | "Student ID must be a positive whole number." |
| T-22 | No match | Search Name = `zzzz` | "No students match your search." |
| T-23 | View details | Select 1001, click View Details | Dialog shows all fields + Total 360, Average 90.00, Grade A+ |
| T-24 | Add marks | Marks screen, pick 1007, subject `Accounting`, marks `72` | Row added; summary shows 1 subject, 72.00, Grade B |
| T-25 | Marks range | Marks `101` or `-5` | "Marks must be between 0 and 100." |
| T-26 | Marks not a number | Marks `abc` | "Marks must be a number, for example 78 or 78.5." |
| T-27 | Duplicate subject | Add `Data Structures` again for 1001 | "This student already has marks for 'Data Structures'..." |
| T-28 | Update marks | Select a mark row, change value, Update | Table and summary refresh |
| T-29 | Delete marks | Select a mark row, Delete, confirm | Row removed; summary recalculated |
| T-30 | Results screen | Open Results | Table matches the sample-data table above; A+/A green, F red |
| T-31 | Navigation | Dashboard -> Students -> Back -> Marks -> Back -> Results -> Back | Each screen opens and returns to dashboard |
| T-32 | DB down | Stop MySQL, try to login | "Cannot connect to the database..." (no stack trace in UI) |
| T-33 | Wrong DB password | Set wrong `db.password`, login | "MySQL rejected the username or password..." |

## Grade boundary checks
| Percentage | Grade |
|---|---|
| 100, 90 | A+ |
| 89.99, 80 | A |
| 79.99, 70 | B |
| 69.99, 60 | C |
| 59.99, 50 | D |
| 49.99, 0 | F |

## Common errors and solutions

| Error | Cause | Solution |
|---|---|---|
| `application.properties not found` | Config file missing | Copy `application.properties.example` to `application.properties` in `src/main/resources` |
| "MySQL rejected the username or password" | Wrong `db.username`/`db.password` | Edit `application.properties` (no quotes around password) |
| "Cannot connect to the database" | MySQL stopped, wrong port/host | Start MySQL; check port 3306 in `db.url` |
| "Database 'student_management' not found" | SQL script not run | Run `sql/student_management.sql` |
| `Public Key Retrieval is not allowed` | Missing URL option | Keep `allowPublicKeyRetrieval=true` in `db.url` |
| `No suitable driver found` | Dependency not downloaded | Maven -> Reload Project; check internet connection |
| SQL script fails at `CHECK` | MySQL older than 8.0.16 | Upgrade to MySQL 8.x |
| `invalid source release: 17` | JDK older than 17 | Install JDK 17+ and set it as the project SDK |
| Changes to `application.properties` ignored | Old build in `target/` | Rebuild the project (Build -> Rebuild Project) |
| Login fails with correct password | Admin row inserted with a different salt/hash formula | Re-run the SQL script, or use the INSERT snippet in the README |
