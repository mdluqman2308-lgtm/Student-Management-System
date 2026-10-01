package com.studentmanagement.dao;

import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.DashboardStats;
import com.studentmanagement.model.SearchField;
import com.studentmanagement.model.Student;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Database access for the students table. All SQL uses PreparedStatement parameters. */
public class StudentDAO {

    private static final String SELECT_ALL =
            "SELECT student_id, name, email, phone, gender, date_of_birth, department, course, `year` FROM students";

    private static final String INSERT =
            "INSERT INTO students (name, email, phone, gender, date_of_birth, department, course, `year`) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE students SET name = ?, email = ?, phone = ?, gender = ?, date_of_birth = ?, "
                    + "department = ?, course = ?, `year` = ? WHERE student_id = ?";

    private static final String DELETE = "DELETE FROM students WHERE student_id = ?";
    private static final String EMAIL_EXISTS =
            "SELECT COUNT(*) FROM students WHERE email = ? AND student_id <> ?";
    private static final String STATS =
            "SELECT COUNT(*), COUNT(DISTINCT department), COUNT(DISTINCT course) FROM students";

    public int insert(Student student) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            bindStudentFields(statement, student);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("The database did not return the new student id.");
        }
    }

    /** @return number of rows updated (0 if the student does not exist) */
    public int update(Student student) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            bindStudentFields(statement, student);
            statement.setInt(9, student.getStudentId());
            return statement.executeUpdate();
        }
    }

    /** @return number of rows deleted (0 if the student does not exist) */
    public int delete(int studentId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, studentId);
            return statement.executeUpdate();
        }
    }

    public Optional<Student> findById(int studentId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " WHERE student_id = ?")) {
            statement.setInt(1, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL + " ORDER BY student_id");
             ResultSet rs = statement.executeQuery()) {
            return mapRows(rs);
        }
    }

    /**
     * Searches by one field. STUDENT_ID is an exact match; the other fields use a
     * case-insensitive "contains" match. The keyword for STUDENT_ID must be a valid integer.
     */
    public List<Student> search(SearchField field, String keyword) throws SQLException {
        String sql = switch (field) {
            case STUDENT_ID -> SELECT_ALL + " WHERE student_id = ? ORDER BY student_id";
            case NAME -> SELECT_ALL + " WHERE name LIKE ? ORDER BY student_id";
            case DEPARTMENT -> SELECT_ALL + " WHERE department LIKE ? ORDER BY student_id";
            case COURSE -> SELECT_ALL + " WHERE course LIKE ? ORDER BY student_id";
        };
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (field == SearchField.STUDENT_ID) {
                statement.setInt(1, Integer.parseInt(keyword));
            } else {
                statement.setString(1, "%" + escapeLikeWildcards(keyword) + "%");
            }
            try (ResultSet rs = statement.executeQuery()) {
                return mapRows(rs);
            }
        }
    }

    /** @param excludedStudentId the student being edited (use 0 when adding a new student) */
    public boolean emailExists(String email, int excludedStudentId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(EMAIL_EXISTS)) {
            statement.setString(1, email);
            statement.setInt(2, excludedStudentId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public DashboardStats getStats() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(STATS);
             ResultSet rs = statement.executeQuery()) {
            rs.next();
            return new DashboardStats(rs.getInt(1), rs.getInt(2), rs.getInt(3));
        }
    }

    private void bindStudentFields(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getName());
        statement.setString(2, student.getEmail());
        statement.setString(3, student.getPhone());
        statement.setString(4, student.getGender());
        statement.setDate(5, Date.valueOf(student.getDateOfBirth()));
        statement.setString(6, student.getDepartment());
        statement.setString(7, student.getCourse());
        statement.setInt(8, student.getYear());
    }

    private List<Student> mapRows(ResultSet rs) throws SQLException {
        List<Student> students = new ArrayList<>();
        while (rs.next()) {
            students.add(mapRow(rs));
        }
        return students;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setGender(rs.getString("gender"));
        student.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        student.setDepartment(rs.getString("department"));
        student.setCourse(rs.getString("course"));
        student.setYear(rs.getInt("year"));
        return student;
    }

    private String escapeLikeWildcards(String keyword) {
        return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
