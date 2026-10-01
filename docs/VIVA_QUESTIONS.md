# Likely Viva Questions and Answers

## Project and architecture
**1. Explain your project in two lines.**
A Java Swing desktop application with MySQL that lets an admin log in, manage students and marks, and view automatically calculated results.

**2. Which architecture did you use and why?**
MVC with the DAO pattern. Models hold data, Swing frames are the views, services hold business rules and DAOs hold all SQL. Responsibilities are separated, so each layer can change without breaking the others.

**3. What is the DAO pattern?**
Data Access Object: a class that hides all database code for one entity (`StudentDAO`, `MarkDAO`, `AdminDAO`). The rest of the program calls methods like `findAll()` without knowing SQL.

**4. Why a service layer between UI and DAO?**
It holds business logic: validation, duplicate checks and result calculation. The UI stays thin and the DAO stays focused on SQL.

**5. Why no Spring Boot or Hibernate?**
The aim was to demonstrate core Java, JDBC and Swing directly, so I understand what frameworks normally hide.

**6. What is `Navigator` for?**
It creates the services once and switches screens. Only one frame is open at a time; showing a new one disposes the old one. Logout calls `Navigator.logout()` which returns to the login screen.

## JDBC and database
**7. What is JDBC?**
Java Database Connectivity: the standard API (`Connection`, `PreparedStatement`, `ResultSet`) for talking to relational databases.

**8. What is the MySQL Connector/J?**
The JDBC driver that implements the JDBC interfaces for MySQL. Maven downloads it from the `mysql-connector-j` dependency.

**9. Why `PreparedStatement` instead of `Statement`?**
It sends parameters separately from the SQL, which prevents SQL injection, and the database can reuse the compiled statement.

**10. What is SQL injection? Show an example.**
Input such as `' OR '1'='1` concatenated into a query can change its meaning and bypass login. With `?` placeholders the input is always treated as data, never as SQL.

**11. What is try-with-resources?**
A `try (...)` block that automatically closes `Connection`, `PreparedStatement` and `ResultSet` even if an exception occurs, preventing resource leaks.

**12. How do you avoid hard-coding credentials?**
`DatabaseConnection` reads `application.properties` (or environment variables). The file is in `.gitignore`.

**13. Explain the foreign key in your project.**
`marks.student_id` references `students.student_id`. A mark cannot exist without a student, and `ON DELETE CASCADE` removes marks when the student is deleted.

**14. Why is `student_id` AUTO_INCREMENT?**
MySQL generates unique IDs automatically. Java reads the new ID using `Statement.RETURN_GENERATED_KEYS`.

**15. What constraints did you use?**
Primary keys, foreign key, UNIQUE (username, email, student+subject), NOT NULL and CHECK (phone, gender, year, marks).

**16. Why indexes on name, department and course?**
They speed up searching and filtering on those columns.

**17. What is normalization? Is your database normalized?**
Organizing tables to reduce redundancy. Each table stores one kind of data and marks reference students by ID, so it is in 3NF.

**18. Difference between DELETE and ON DELETE CASCADE?**
DELETE removes the chosen row. CASCADE automatically removes child rows (marks) that depend on it.

## Security
**19. How are passwords stored?**
As SHA-256(salt + password) in hex. The salt is stored in its own column.

**20. What is a salt and why use it?**
A random string added before hashing so identical passwords get different hashes and precomputed (rainbow) tables are useless.

**21. Is SHA-256 enough for real systems?**
No. It is fast, so it is easier to brute-force. Real systems use slow algorithms like BCrypt, PBKDF2 or Argon2. I mention this as a future enhancement.

**22. Why compare hashes with `MessageDigest.isEqual`?**
It takes the same time regardless of where the strings differ, preventing timing attacks.

**23. Why not show stack traces to users?**
They confuse users and can leak internal details. The app shows friendly messages and logs the technical detail to the console.

## Java and OOP
**24. Which OOP concepts did you use?**
Encapsulation (private fields with getters/setters), abstraction (DAO and service hide details), inheritance (`JFrame`, `Exception`), polymorphism (`toString`, overridden methods, `Runnable` lambdas) and composition (services own DAOs).

**25. What is encapsulation in your code?**
Model fields are private and accessed through getters/setters.

**26. What is a checked exception? Give an example from the project.**
An exception the compiler forces you to handle, such as `SQLException` and my `ServiceException`.

**27. Why create `ServiceException`?**
To carry a user-friendly message from services to the UI without exposing SQL details.

**28. Which collections did you use?**
`List<Student>`, `List<Mark>`, and `Map<Integer, List<Mark>>` for grouping marks by student.

**29. What is a Java record? Where did you use it?**
A compact immutable data class (Java 16+). I used `StudentResult` and `DashboardStats`.

**30. What is `Optional` and why use it?**
A container that may or may not hold a value, avoiding `null` returns. `AdminDAO.findByUsername` returns `Optional<Admin>`.

**31. What does `static` mean in `GradeUtil` and `ValidationUtil`?**
They are utility classes with no state, so methods are static and the constructor is private.

**32. What is the Single Responsibility Principle?**
A class should have one reason to change. `StudentDAO` only does SQL, `GradeUtil` only grades, `StudentFrame` only builds the screen.

## Swing and GUI
**33. What is Swing? How is it different from AWT?**
Swing is Java's GUI toolkit built on AWT. Its components are lightweight, drawn by Java, and look the same on every OS.

**34. What layout managers did you use?**
`BorderLayout`, `GridBagLayout` (forms), `GridLayout` (dashboard cards and buttons) and `FlowLayout` (button rows).

**35. How does `JTable` work?**
It displays data from a `TableModel`. I used `DefaultTableModel`, made cells non-editable, and refresh by clearing and re-adding rows.

**36. How does selecting a table row fill the form?**
A `ListSelectionListener` on the table's selection model reads the selected row and sets each field.

**37. Why `SwingUtilities.invokeLater` in `Main`?**
Swing is not thread-safe; UI must be created on the Event Dispatch Thread.

**38. What is `JOptionPane`?**
A ready-made dialog for messages and confirmations. I wrapped it in a `Dialogs` class.

## Business logic
**39. How is the grade calculated?**
Percentage = total / (subjects x 100) x 100. 90+ A+, 80+ A, 70+ B, 60+ C, 50+ D, below 50 F.

**40. Why is the average equal to the percentage?**
Every subject is out of 100, so both give the same number.

**41. How do you validate marks?**
Must be a number (not NaN/infinity) between 0 and 100. Checked in `MarkService`, and again by a MySQL CHECK constraint.

**42. How do you prevent duplicate emails?**
The service checks `emailExists()` first and the database has a UNIQUE constraint as a safety net.

**43. What happens if MySQL is not running?**
The SQLException is translated into "Cannot connect to the database..." and shown in a dialog; the app does not crash.

**44. How would you extend the project?**
Add roles, BCrypt, PDF export, attendance and fees modules, or move to Spring Boot with a web UI.

**45. What testing did you do?**
JUnit tests for grading, validation, hashing and result calculation, plus 33 manual test cases covering every screen.
