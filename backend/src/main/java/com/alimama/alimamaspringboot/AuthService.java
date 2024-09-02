package com.alimama.alimamaspringboot;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.naming.AuthenticationException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class AuthService {

    private final DatabaseConnection databaseConnection;
    private final Key key;

    @Autowired
    public AuthService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // HMAC-SHA256 key
    }

    public String authenticate(String email, String password) throws AuthenticationException {
        String query =
                "SELECT pwd_hash " +
                "FROM users u " +
                "JOIN login_info l ON u.user_id = l.user_id " +
                "WHERE u.email = ?";

        try (PreparedStatement stmt = databaseConnection.getPostgresqlClient().prepareStatement(query)) {
            stmt.setString(1, email); // Bind the email parameter to the query
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
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
