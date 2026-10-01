package com.studentmanagement.service;

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Converts raw SQLExceptions into user-friendly ServiceExceptions. */
final class DatabaseErrors {

    private static final Logger LOGGER = Logger.getLogger(DatabaseErrors.class.getName());

    private static final int ERROR_DUPLICATE_ENTRY = 1062;
    private static final int ERROR_FOREIGN_KEY_NO_PARENT = 1452;
    private static final int ERROR_UNKNOWN_DATABASE = 1049;
    private static final int ERROR_CHECK_VIOLATED = 3819;

    private DatabaseErrors() {
    }

    static ServiceException translate(SQLException e) {
        LOGGER.log(Level.SEVERE, "Database error: " + e.getMessage(), e);
        String state = e.getSQLState();

        if (e.getErrorCode() == ERROR_FOREIGN_KEY_NO_PARENT) {
            return new ServiceException("The selected student does not exist any more.", e);
        }
        if (e.getErrorCode() == ERROR_DUPLICATE_ENTRY) {
            return new ServiceException("A record with the same unique value already exists.", e);
        }
        if (e.getErrorCode() == ERROR_CHECK_VIOLATED) {
            return new ServiceException("The data breaks a database rule. Please check the values.", e);
        }
        if (e.getErrorCode() == ERROR_UNKNOWN_DATABASE) {
            return new ServiceException(
                    "Database 'student_management' not found. Run sql/student_management.sql first.", e);
        }
        if ("28000".equals(state)) {
            return new ServiceException(
                    "MySQL rejected the username or password. Check db.username and db.password "
                            + "in application.properties.", e);
        }
        if (e instanceof SQLRecoverableException || (state != null && state.startsWith("08"))) {
            return new ServiceException(
                    "Cannot connect to the database. Make sure MySQL is running and the settings in "
                            + "application.properties are correct.", e);
        }
        return new ServiceException("A database error occurred. Please try again.", e);
    }
}
