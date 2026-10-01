-- =====================================================
-- Student Management System - Database Setup
-- Requires MySQL 8.0.16+ (CHECK constraints are enforced)
-- WARNING: this script DROPS and recreates the database.
-- =====================================================

DROP DATABASE IF EXISTS student_management;
CREATE DATABASE student_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE student_management;

-- -----------------------------------------------------
-- admins
-- password column stores SHA-256( salt + plain_password ) as 64 hex chars
-- -----------------------------------------------------
CREATE TABLE admins (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    password   CHAR(64)     NOT NULL,
    salt       VARCHAR(32)  NOT NULL,
    CONSTRAINT uq_admins_username UNIQUE (username)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- students
-- -----------------------------------------------------
CREATE TABLE students (
    student_id     INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(120) NOT NULL,
    phone          VARCHAR(10)  NOT NULL,
    gender         VARCHAR(10)  NOT NULL,
    date_of_birth  DATE         NOT NULL,
    department     VARCHAR(80)  NOT NULL,
    course         VARCHAR(80)  NOT NULL,
    `year`         TINYINT      NOT NULL,
    CONSTRAINT uq_students_email   UNIQUE (email),
    CONSTRAINT chk_students_phone  CHECK (phone REGEXP '^[0-9]{10}$'),
    CONSTRAINT chk_students_gender CHECK (gender IN ('Male', 'Female', 'Other')),
    CONSTRAINT chk_students_year   CHECK (`year` BETWEEN 1 AND 4),
    INDEX idx_students_name       (name),
    INDEX idx_students_department (department),
    INDEX idx_students_course     (course)
) ENGINE=InnoDB AUTO_INCREMENT=1001;

-- -----------------------------------------------------
-- marks
-- A student can have only one mark row per subject.
-- Deleting a student automatically deletes their marks.
-- -----------------------------------------------------
CREATE TABLE marks (
    mark_id     INT AUTO_INCREMENT PRIMARY KEY,
    student_id  INT          NOT NULL,
    subject     VARCHAR(80)  NOT NULL,
    marks       DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_marks_student
        FOREIGN KEY (student_id) REFERENCES students (student_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT uq_marks_student_subject UNIQUE (student_id, subject),
    CONSTRAINT chk_marks_range CHECK (marks BETWEEN 0 AND 100),
    INDEX idx_marks_student (student_id)
) ENGINE=InnoDB;

-- =====================================================
-- SAMPLE DATA
-- =====================================================

-- Default admin: username = admin , password = Admin@123
-- Hash is computed by MySQL as SHA2(salt + password, 256); the Java code uses the same formula.
SET @salt = 'c3a9f1e27b4d4e68';
INSERT INTO admins (username, password, salt)
VALUES ('admin', SHA2(CONCAT(@salt, 'Admin@123'), 256), @salt);

-- Students (IDs will be 1001 to 1008)
INSERT INTO students (name, email, phone, gender, date_of_birth, department, course, `year`) VALUES
('Aarav Sharma',   'aarav.sharma@example.com',   '9876543210', 'Male',   '2004-03-15', 'Computer Science', 'B.Tech CSE',        3),
('Priya Reddy',    'priya.reddy@example.com',    '9123456780', 'Female', '2005-07-22', 'Computer Science', 'B.Tech CSE',        2),
('Rohan Verma',    'rohan.verma@example.com',    '9988776655', 'Male',   '2003-11-02', 'Electronics',      'B.Tech ECE',        4),
('Sneha Iyer',     'sneha.iyer@example.com',     '9012345678', 'Female', '2004-01-30', 'Electronics',      'B.Tech ECE',        3),
('Kiran Kumar',    'kiran.kumar@example.com',    '9765432109', 'Male',   '2005-09-12', 'Mechanical',       'B.Tech Mechanical', 1),
('Ananya Das',     'ananya.das@example.com',     '9345678901', 'Female', '2004-05-18', 'Business Admin',   'BBA',               2),
('Vikram Singh',   'vikram.singh@example.com',   '9567890123', 'Male',   '2003-12-25', 'Business Admin',   'MBA',               1),
('Meera Nair',     'meera.nair@example.com',     '9456781230', 'Other',  '2004-08-09', 'Computer Science', 'BCA',               2);

-- Marks
INSERT INTO marks (student_id, subject, marks) VALUES
-- 1001 Aarav: 95, 88, 92, 85  -> total 360, avg 90.00 -> A+
(1001, 'Data Structures',   95),
(1001, 'Database Systems',  88),
(1001, 'Operating Systems', 92),
(1001, 'Java Programming',  85),
-- 1002 Priya: 82, 78, 85 -> total 245, avg 81.67 -> A
(1002, 'Data Structures',   82),
(1002, 'Database Systems',  78),
(1002, 'Java Programming',  85),
-- 1003 Rohan: 65, 70, 58 -> total 193, avg 64.33 -> C
(1003, 'Digital Electronics', 65),
(1003, 'Signals and Systems', 70),
(1003, 'VLSI Design',         58),
-- 1004 Sneha: 74, 71, 79 -> total 224, avg 74.67 -> B
(1004, 'Digital Electronics', 74),
(1004, 'Signals and Systems', 71),
(1004, 'Microprocessors',     79),
-- 1005 Kiran: 45, 52, 38 -> total 135, avg 45.00 -> F
(1005, 'Thermodynamics',      45),
(1005, 'Engineering Drawing', 52),
(1005, 'Fluid Mechanics',     38),
-- 1006 Ananya: 55, 60 -> total 115, avg 57.50 -> D
(1006, 'Accounting',  55),
(1006, 'Marketing',   60);
-- 1007 Vikram and 1008 Meera intentionally have NO marks (tests the "no marks yet" case)

-- Quick verification
SELECT COUNT(*) AS students_loaded FROM students;
SELECT COUNT(*) AS marks_loaded FROM marks;
SELECT username, LENGTH(password) AS hash_length FROM admins;
