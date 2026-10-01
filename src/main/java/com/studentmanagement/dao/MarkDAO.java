package com.studentmanagement.dao;

import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.Mark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Database access for the marks table. All SQL uses PreparedStatement parameters. */
public class MarkDAO {

    private static final String SELECT = "SELECT mark_id, student_id, subject, marks FROM marks";
    private static final String INSERT = "INSERT INTO marks (student_id, subject, marks) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE marks SET subject = ?, marks = ? WHERE mark_id = ?";
    private static final String DELETE = "DELETE FROM marks WHERE mark_id = ?";
    private static final String SUBJECT_EXISTS =
            "SELECT COUNT(*) FROM marks WHERE student_id = ? AND subject = ? AND mark_id <> ?";

    public int insert(Mark mark) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, mark.getStudentId());
            statement.setString(2, mark.getSubject());
            statement.setDouble(3, mark.getMarks());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("The database did not return the new mark id.");
        }
    }

    /** @return number of rows updated (0 if the mark does not exist) */
    public int update(Mark mark) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, mark.getSubject());
            statement.setDouble(2, mark.getMarks());
            statement.setInt(3, mark.getMarkId());
            return statement.executeUpdate();
        }
    }

    /** @return number of rows deleted (0 if the mark does not exist) */
    public int delete(int markId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setInt(1, markId);
            return statement.executeUpdate();
        }
    }

    public List<Mark> findByStudent(int studentId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(SELECT + " WHERE student_id = ? ORDER BY subject")) {
            statement.setInt(1, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                return mapRows(rs);
            }
        }
    }

    public List<Mark> findAll() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT + " ORDER BY student_id, subject");
             ResultSet rs = statement.executeQuery()) {
            return mapRows(rs);
        }
    }

    /** @param excludedMarkId the mark being edited (use 0 when adding a new mark) */
    public boolean subjectExists(int studentId, String subject, int excludedMarkId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SUBJECT_EXISTS)) {
            statement.setInt(1, studentId);
            statement.setString(2, subject);
            statement.setInt(3, excludedMarkId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private List<Mark> mapRows(ResultSet rs) throws SQLException {
        List<Mark> marks = new ArrayList<>();
        while (rs.next()) {
            Mark mark = new Mark();
            mark.setMarkId(rs.getInt("mark_id"));
            mark.setStudentId(rs.getInt("student_id"));
            mark.setSubject(rs.getString("subject"));
            mark.setMarks(rs.getDouble("marks"));
            marks.add(mark);
        }
        return marks;
    }
}
