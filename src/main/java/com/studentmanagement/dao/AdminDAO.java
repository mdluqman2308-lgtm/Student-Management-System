package com.studentmanagement.dao;

import com.studentmanagement.database.DatabaseConnection;
import com.studentmanagement.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/** Database access for the admins table. */
public class AdminDAO {

    private static final String FIND_BY_USERNAME =
            "SELECT id, username, password, salt FROM admins WHERE username = ?";

    public Optional<Admin> findByUsername(String username) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password"));
                admin.setSalt(rs.getString("salt"));
                return Optional.of(admin);
            }
        }
    }
}
