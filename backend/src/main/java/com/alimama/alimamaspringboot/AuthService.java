package com.alimama.alimamaspringboot;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.security.Key;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Service
public class AuthService {

    private final DatabaseConnection databaseConnection;
    private final Key key;

    @Autowired
    public AuthService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HMAC-SHA256 key
    }

    public void registerUser(RegisterRequest registerRequest) throws Exception {
        Connection conn = null;

        try {
            // Ensure PostgreSQL connection
            if (!databaseConnection.connectPostgres()) {
                throw new SQLException("Failed to connect to PostgreSQL.");
            }

            conn = databaseConnection.getPostgresqlClient();
            conn.setAutoCommit(false); // Start transaction

            // Check if email already exists
            String checkEmailQuery = String.format(
                    "SELECT email FROM users WHERE email = '%s'",
                    registerRequest.getEmail()
            );

            ResultSet rs = databaseConnection.querySelectPostgres(checkEmailQuery);

            if (rs != null && rs.next()) {
                System.out.println("Email found: " + rs.getString("email"));
                throw new Exception("Email already in use");
            } else {
                System.out.println("No email found, proceeding with registration.");
            }

            // Insert into users table
            String insertUserQuery = String.format(
                    "INSERT INTO users (email, tckn, user_role, created_at) VALUES ('%s', '%s', '%s', CURRENT_DATE) RETURNING user_id",
                    registerRequest.getEmail(),
                    registerRequest.getTckn(),
                    registerRequest.getRole()
            );
            rs = databaseConnection.querySelectPostgres(insertUserQuery);

            int userId;
            if (rs != null && rs.next()) {
                userId = rs.getInt("user_id");
            } else {
                throw new SQLException("Failed to retrieve user ID.");
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt());

            // Insert into login_info table
            String insertLoginInfoQuery = String.format(
                    "INSERT INTO login_info (user_id, pwd_hash) VALUES (%d, '%s')",
                    userId,
                    hashedPassword
            );
            boolean loginInfoInserted = databaseConnection.queryUpdatePostgres(insertLoginInfoQuery);
            if (!loginInfoInserted) {
                throw new SQLException("Failed to insert login info.");
            }

            // Insert into role-specific table
            if (registerRequest.getRole().equalsIgnoreCase("Customer")) {
                String insertCustomerQuery = String.format(
                        "INSERT INTO customer (user_id, full_name) VALUES (%d, '%s')",
                        userId,
                        registerRequest.getFullName()
                );
                boolean customerInserted = databaseConnection.queryUpdatePostgres(insertCustomerQuery);
                if (!customerInserted) {
                    throw new SQLException("Failed to insert customer info.");
                }
            } else if (registerRequest.getRole().equalsIgnoreCase("Vendor")) {
                String insertVendorQuery = String.format(
                        "INSERT INTO vendor (user_id, brand_name) VALUES (%d, '%s')",
                        userId,
                        registerRequest.getBrandName()
                );
                boolean vendorInserted = databaseConnection.queryUpdatePostgres(insertVendorQuery);
                if (!vendorInserted) {
                    throw new SQLException("Failed to insert vendor info.");
                }
            }

            conn.commit(); // Commit transaction
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    throw new Exception("Failed to rollback transaction: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw e; // Rethrow the original exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset to default auto-commit mode
                } catch (SQLException ignore) {
                }
            }
        }
    }

    public String authenticate(String email, String password) throws AuthenticationException {
        if (!databaseConnection.connectPostgres()) {
            throw new AuthenticationException("Failed to connect to PostgreSQL.");
        }

        String query = String.format(
                "SELECT pwd_hash FROM users u JOIN login_info l ON u.user_id = l.user_id WHERE u.email = '%s'",
                email
        );
        try (ResultSet rs = databaseConnection.querySelectPostgres(query)) {
            if (rs != null && rs.next()) {
                String storedHash = rs.getString("pwd_hash");
                if (BCrypt.checkpw(password, storedHash)) {
                    return generateToken(email);
                }
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Error during authentication: " + e.getMessage());
        }
        throw new AuthenticationException("Invalid credentials");
    }

    private String generateToken(String email) {
        long now = System.currentTimeMillis();
        long expirationTime = 3600000; // 1 hour in ms

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(key)
                .compact();
    }
}
