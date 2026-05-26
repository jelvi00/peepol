package org.peepol.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.paseto4j.commons.SecretKey;
import org.paseto4j.commons.Version;
import org.paseto4j.version4.PasetoLocal;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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

    @Test
    void getSubjectExpiredToken() throws Exception {

        String jsonPayload = new ObjectMapper().writeValueAsString(
                new Object() {
                    public final String sub = "expireduser";
                    public final String exp = Instant.now().minus(1, ChronoUnit.HOURS).toString();
                }
        );

        SecretKey sk = new SecretKey(secretKey.getBytes(), Version.V4);
        String expiredToken = PasetoLocal.encrypt(sk, jsonPayload, "", "");

        String subject = pasetoManager.getSubject(expiredToken);
        assertNull(subject, "Should return null for expired token");
    }
}
