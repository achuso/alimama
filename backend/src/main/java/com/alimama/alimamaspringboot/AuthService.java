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
                stmt.setString(2, registerRequest.getLegal_name());
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
                stmt.setString(3, registerRequest.getLegal_name());
                stmt.setString(4, registerRequest.getRole());
                System.out.printf("New registration:\n%s\n%s\n%s\n%s\n",
                        registerRequest.getEmail(), registerRequest.getTckn(),
                        registerRequest.getLegal_name(), registerRequest.getRole());

                ResultSet rs = stmt.executeQuery();

                if (rs != null && rs.next()) {
                    userId = rs.getInt("user_id");
                }
                else {
                    throw new SQLException("Failed to retrieve user ID.");
                }
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt());

            // Insert into login_info table
            String insertLoginInfoQuery = "INSERT INTO login_info (user_id, pwd_hash) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertLoginInfoQuery)) {
                stmt.setInt(1, userId);
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
                } catch (SQLException ignore) {
                }
            }
        }
    }

    public String authenticate(String email, String password) throws AuthenticationException {
        if (!databaseConnection.connectPostgres()) {
            throw new AuthenticationException("Failed to connect to PostgreSQL.");
        }

        String query = "SELECT pwd_hash FROM users u JOIN login_info l ON u.user_id = l.user_id WHERE u.email = ?";
        try (PreparedStatement stmt = databaseConnection.getPostgresqlClient().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs != null && rs.next()) {
                String storedHash = rs.getString("pwd_hash");
                if (BCrypt.checkpw(password, storedHash))
                    return generateToken(email);
            }
        }
        catch (SQLException e) {
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
