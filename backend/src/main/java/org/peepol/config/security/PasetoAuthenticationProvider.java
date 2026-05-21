package org.peepol.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PasetoAuthenticationProvider implements AuthenticationProvider {

    private final PasetoManager pasetoManager;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            if (Objects.isNull(authentication))
                throw new IllegalArgumentException();

            String token = (String) authentication.getCredentials();
            if (Objects.isNull(token) || token.isBlank())
                throw new IllegalArgumentException();

            String username = pasetoManager.getSubject(token);
            if (Objects.isNull(username) || username.isBlank())
                throw new IllegalArgumentException();

            var userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

        } catch (Exception e) {
            throw new BadCredentialsException("Invalid PASETO token provided.", e);
        }


    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasetoAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
