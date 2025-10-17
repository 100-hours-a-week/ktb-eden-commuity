package com.example.restapi_subject.global.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    private static final int WORK_FACTOR = 12;

    public String hash(String raw) {
        String salt = BCrypt.gensalt(WORK_FACTOR);
        return BCrypt.hashpw(raw, salt);
    }

    public boolean matches(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}
