package com.alimama.alimamaspringboot;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordTest {
    public static void main(String[] args) {
        String password = "123";
        String storedHash = "$2a$10$AV1jL4Yn74YmfKu2nQozc.vuuFV9K5OV3ZGemdDMZDkEGkPGDkYDq";

        // Manually generating the hash for comparison
        String correctHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Generated hash for '123': " + correctHash);

        // Verifying password against stored hash
        boolean isMatch = BCrypt.checkpw(password, storedHash);
        System.out.println("Password match: " + isMatch);
    }


}
