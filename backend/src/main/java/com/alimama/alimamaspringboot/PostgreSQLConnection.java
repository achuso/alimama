package com.alimama.alimamaspringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class PostgreSQLConnection {
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLConnection.class);

    private Connection postgresqlClient;
    private final String postgresConnURI;
    private final String pgUser;
    private final String pgPassword;

    public PostgreSQLConnection() {
        // PostgreSQL credentials with defaults for Dockerized setup
        String pgHost = getEnv("POSTGRES_HOST", "localhost");
        String pgPort = getEnv("POSTGRES_PORT", "5432");
        String pgDbName = getEnv("POSTGRES_DB_NAME", "alimama_db");
        this.pgUser = getEnv("POSTGRES_USER", "alimama_user");
        this.pgPassword = getEnv("POSTGRES_PASSWORD", "alimama_password");

        this.postgresConnURI = String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDbName);
        logger.info("PostgreSQL connection URI initialized: {}", postgresConnURI);
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            if (defaultValue != null) {
                logger.warn("Environment variable '{}' not found. Using default: {}", key, defaultValue);
                return defaultValue;
            }
            throw new IllegalArgumentException("Missing required environment variable: " + key);
        }
        return value;
    }

    public boolean connectPostgres() {
        if (this.postgresqlClient == null) {
            try {
                this.postgresqlClient = DriverManager.getConnection(this.postgresConnURI, this.pgUser, this.pgPassword);
                logger.info("Successfully connected to PostgreSQL database.");
                return true;
            } 
            catch (SQLException e) {
                logger.error("Failed to connect to PostgreSQL: {}", e.getMessage(), e);
                return false;
            }
        }
        
        logger.info("PostgreSQL client already connected.");
        return true;
    }

    public ResultSet querySelectPostgres(String query) {
        ResultSet results = null;

        if (this.postgresqlClient == null) {
            logger.error("PostgreSQL client is not connected.");
            return results;
        }

        try (PreparedStatement stmt = this.postgresqlClient.prepareStatement(query)) {
            logger.info("Executing query: {}", query);
            results = stmt.executeQuery();
        } 
        catch (SQLException e) {
            logger.error("SQL select error: {}", e.getMessage(), e);
        }

        return results;
    }

    public boolean queryUpdatePostgres(String query) {
        if (this.postgresqlClient == null) {
            logger.error("PostgreSQL client is not connected.");
            return false;
        }

        boolean success = false;

        try (PreparedStatement stmt = this.postgresqlClient.prepareStatement(query)) {
            this.postgresqlClient.setAutoCommit(false);
            logger.info("Executing update: {}", query);
            stmt.executeUpdate();
            this.postgresqlClient.commit();
            success = true;
            logger.info("Update query executed successfully.");
        } 
        catch (SQLException e) {
            logger.error("SQL update error: {}", e.getMessage(), e);
            try {
                this.postgresqlClient.rollback();
                logger.warn("Transaction rollback successful.");
            } 
            catch (SQLException rollbackEx) {
                logger.error("Rollback error: {}", rollbackEx.getMessage(), rollbackEx);
            }
        } 
        finally {
            try {
                this.postgresqlClient.setAutoCommit(true);
            } 
            catch (SQLException e) {
                logger.error("Error resetting auto-commit: {}", e.getMessage(), e);
            }
        }

        return success;
    }

    public boolean disconnectPostgres() {
        if (this.postgresqlClient != null) {
            try {
                this.postgresqlClient.close();
                logger.info("PostgreSQL connection closed successfully.");
                return true;
            } 
            catch (SQLException e) {
                logger.error("Error closing PostgreSQL connection: {}", e.getMessage(), e);
                return false;
            }
        }
        logger.warn("PostgreSQL client was not connected.");
        return false;
    }

    public Connection getPostgresqlClient() {
        return this.postgresqlClient;
    }
}
