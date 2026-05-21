package org.peepol.config.security;


import org.paseto4j.commons.SecretKey;
import org.paseto4j.commons.Version;
import org.paseto4j.version4.PasetoLocal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class PasetoManager {

    private final SecretKey secretKey;

    public PasetoManager(@Value("${app.paseto.secret_key:no-key}") String pasetoKey) {
        this.secretKey = new SecretKey(pasetoKey.getBytes(), Version.V4);
    }

    public String createToken(String username) {
        String payload = "{\"sub\":" + username + ", \"exp\":" + Instant.now().plusSeconds(86400) + "\"}";
        return PasetoLocal.encrypt(secretKey, payload, "", "");
    }

    public String getSubject(String token) {
        try {
            String decrypted = PasetoLocal.decrypt(secretKey, token, "");
            int subIndex = decrypted.indexOf("\"sub\":");
            if (subIndex == -1) return null;

            String afterSub = decrypted.substring(subIndex + 6);
            int commaIndex = afterSub.indexOf(",");
            int braceIndex = afterSub.indexOf("}");

            int endIndex;
            if (commaIndex == -1) endIndex = braceIndex;
            else if (braceIndex == -1) endIndex = commaIndex;
            else endIndex = Math.min(commaIndex, braceIndex);

            if (endIndex == -1) return null;
            return afterSub.substring(0, endIndex);
        } catch (Exception e) {
            return null;
        }
    }

}
