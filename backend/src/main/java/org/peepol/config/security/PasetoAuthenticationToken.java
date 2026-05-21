package org.peepol.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class PasetoAuthenticationToken extends AbstractAuthenticationToken {

    private final Object credentials;

    public PasetoAuthenticationToken(String token) {
        super(null);
        this.credentials = token;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
