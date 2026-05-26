package org.peepol.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasetoManagerTest {

    private PasetoManager pasetoManager;
    private final String secretKey = "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        pasetoManager = new PasetoManager(secretKey, new ObjectMapper());
    }

    @Test
    void createAndGetSubject() {
        String username = "testuser";
        String token = pasetoManager.createToken(username);
        assertNotNull(token);

        String subject = pasetoManager.getSubject(token);
        assertEquals(username, subject);
    }

    @Test
    void getSubjectInvalidToken() {
        String subject = pasetoManager.getSubject("invalid-token");
        assertNull(subject);
    }
}
