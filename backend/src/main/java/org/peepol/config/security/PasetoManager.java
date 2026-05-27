package org.peepol.config.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.paseto4j.commons.SecretKey;
import org.paseto4j.commons.Version;
import org.paseto4j.version4.PasetoLocal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class PasetoManager {

    private final SecretKey secretKey;
    private final ObjectMapper objectMapper;

    public PasetoManager(
            @Value("${app.paseto.secret_key:no-key}") String pasetoKey,
            ObjectMapper objectMapper
    ) {
        this.secretKey = new SecretKey(pasetoKey.getBytes(), Version.V4);
        this.objectMapper = objectMapper;
    }

    public String createToken(String username) {
        try {
            PasetoPayload payload = new PasetoPayload(username, Instant.now().plus(1, ChronoUnit.DAYS).toString());
            String jsonPayload = objectMapper.writeValueAsString(payload);
            return PasetoLocal.encrypt(secretKey, jsonPayload, "", "");
        } catch (Exception e) {
            throw new RuntimeException("Error creating token", e);
        }
    }

    public String getSubject(String token) {
        try {
            String decrypted = PasetoLocal.decrypt(secretKey, token, "");
            PasetoPayload payload = objectMapper.readValue(decrypted, PasetoPayload.class);

            if (payload.exp() != null) {
                Instant expiration = Instant.parse(payload.exp());
                if (expiration.isBefore(Instant.now())) {
                    return null;
                }
            }

            return payload.sub();
        } catch (Exception e) {
            return null;
        }
    }

    private record PasetoPayload(String sub, String exp) {}

}
