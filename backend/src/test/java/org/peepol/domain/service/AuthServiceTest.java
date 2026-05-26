package org.peepol.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peepol.config.security.PasetoManager;
import org.peepol.domain.enums.Role;
import org.peepol.domain.model.User;
import org.peepol.dto.LoginDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PasetoManager pasetoManager;

    @Mock
    private AuthenticationManager authManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginSuccess() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.ADMIN);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(pasetoManager.createToken("testuser")).thenReturn("test-token");

        LoginDTO.Response result = authService.login("testuser", "password");

        assertNotNull(result);
        assertEquals("test-token", result.token());
        assertEquals("testuser", result.username());
    }

    @Test
    void loginBadCredentials() {
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login("testuser", "password"));
    }
}
