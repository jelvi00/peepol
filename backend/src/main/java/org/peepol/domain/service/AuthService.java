package org.peepol.domain.service;

import org.peepol.config.security.PasetoManager;
import org.peepol.domain.model.User;
import org.peepol.dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasetoManager pasetoManager;
    private final AuthenticationManager authManager;


    public LoginDTO.Response login(String username, String password) {

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            User user = (User) authentication.getPrincipal();

            logger.info("Login succeed for user: [{}]", user.getUsername());

            return new LoginDTO.Response(
                    pasetoManager.createToken(user.getUsername()),
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name()
            );

        } catch (Exception e) {
            throw new BadCredentialsException("Username or password is incorrect.");
        }

    }
}
