package com.studentmanagement.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates JDBC connections using settings from application.properties.
 * Environment variables DB_URL, DB_USERNAME and DB_PASSWORD override the file.
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "application.properties";
    private static final String CONNECTION_ERROR_STATE = "08001";

    private static Properties properties;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Properties config = loadProperties();
        String url = resolve("DB_URL", "db.url", config);
        String username = resolve("DB_USERNAME", "db.username", config);
        String password = resolve("DB_PASSWORD", "db.password", config);
        return DriverManager.getConnection(url, username, password);
    }

    private static synchronized Properties loadProperties() throws SQLException {
        if (properties != null) {
            return properties;
        }
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new SQLException(CONFIG_FILE + " not found. Copy application.properties.example to "
                        + CONFIG_FILE + " in src/main/resources.", CONNECTION_ERROR_STATE);
            }
            Properties loaded = new Properties();
            loaded.load(in);
            properties = loaded;
            return properties;
        } catch (IOException e) {
            throw new SQLException("Could not read " + CONFIG_FILE, CONNECTION_ERROR_STATE, e);
        }
    }

    private static String resolve(String environmentName, String propertyName, Properties config)
            throws SQLException {
        String fromEnvironment = System.getenv(environmentName);
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment;
        }
        String value = config.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            throw new SQLException("Missing '" + propertyName + "' in " + CONFIG_FILE, CONNECTION_ERROR_STATE);
        }
        return value.trim();
    }
}
