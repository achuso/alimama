package com.alimama.alimamaspringboot.auth;

import com.alimama.alimamaspringboot.DatabaseConnection;

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
import java.sql.PreparedStatement;

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

            // Check if email or legal_name already exists
            String checkUserQuery = "SELECT email FROM users WHERE email = ? OR legal_name = ?";

            try (PreparedStatement stmt = conn.prepareStatement(checkUserQuery)) {
                stmt.setString(1, registerRequest.getEmail());
                stmt.setString(2, registerRequest.getLegalName());
                ResultSet rs = stmt.executeQuery();

                if (rs != null && rs.next()) {
                    throw new Exception("Email or legal name already in use");
                }
            }

            // Insert into users table
            String insertUserQuery = "INSERT INTO users (email, tckn, legal_name, user_role) VALUES (?, ?, ?, ?::user_role) RETURNING user_id";
            int userId;
            try (PreparedStatement stmt = conn.prepareStatement(insertUserQuery)) {
                stmt.setString(1, registerRequest.getEmail());
                stmt.setString(2, registerRequest.getTckn());
                stmt.setString(3, registerRequest.getLegalName());
                stmt.setString(4, registerRequest.getRole());
                System.out.printf("New registration:\n%s\n%s\n%s\n%s\n",
                        registerRequest.getEmail(), registerRequest.getTckn(),
                        registerRequest.getLegalName(), registerRequest.getRole());

                ResultSet rs = stmt.executeQuery();

                if (rs != null && rs.next()) {
                    userId = rs.getInt("user_id");
                } else {
                    throw new SQLException("Failed to retrieve user ID.");
                }
            }

            // Insert into login_info table
            String insertLoginInfoQuery = "INSERT INTO login_info (user_id, pwd_hash) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertLoginInfoQuery)) {
                stmt.setInt(1, userId);
                // Ensure the password is hashed before storing it, even if it comes hashed from the frontend
                String hashedPassword = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt(10));
                stmt.setString(2, hashedPassword);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected != 1) {
                    throw new SQLException("Failed to insert login info.");
                }
            }

            conn.commit(); // Commit transaction

        }
        catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    throw new Exception("Failed to rollback transaction: " + rollbackEx.getMessage(), rollbackEx);
                }
            }
            throw e; // Rethrow the original exception
        }
        finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset to default auto-commit mode
                }
                catch (SQLException ignore) {}
            }
        }
    }

    public String authenticate(LoginRequest loginRequest) throws AuthenticationException {
        if (!databaseConnection.connectPostgres()) {
            System.err.println("Failed to connect to PostgreSQL.");
            throw new AuthenticationException("Failed to connect to PostgreSQL.");
        }

        String query = "SELECT l.pwd_hash, u.legal_name, u.user_role FROM users u JOIN login_info l ON u.user_id = l.user_id WHERE u.email = ?";
        try (PreparedStatement stmt = databaseConnection.getPostgresqlClient().prepareStatement(query)) {
            stmt.setString(1, loginRequest.getEmail());
            ResultSet rs = stmt.executeQuery();

            if (rs != null && rs.next()) {
                String storedHash = rs.getString("pwd_hash");
                String fullName = rs.getString("legal_name");
                String role = rs.getString("user_role");
                System.out.println("Retrieved password hash: " + storedHash);

                // Compare password from frontend with the stored hash
                if (BCrypt.checkpw(loginRequest.getPassword(), storedHash)) {
                    System.out.println("Password match. Generating token...");
                    return generateToken(loginRequest.getEmail(), fullName, role);
                }
                else {
                    System.err.println("Password mismatch.");
                    throw new AuthenticationException("Invalid credentials");
                }
            }
            else {
                System.err.println("No user found with this email.");
                throw new AuthenticationException("Invalid credentials");
            }
        }
        catch (SQLException e) {
            System.err.println("SQL error during authentication: " + e.getMessage());
            throw new AuthenticationException("Error during authentication: " + e.getMessage());
        }
        catch (Exception e) {
            // Catch any other unexpected exceptions and log them
            System.err.println("Unexpected error during authentication: " + e.getMessage());
            throw new AuthenticationException("Unexpected error occurred.");
        }
    }

    private String generateToken(String email, String fullName, String role) {
        long now = System.currentTimeMillis();
        long expirationTime = 3600000; // 1 hour in ms

        return Jwts.builder()
                .setSubject(email)
                .claim("fullName", fullName)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(key)
                .compact();
    }
}
