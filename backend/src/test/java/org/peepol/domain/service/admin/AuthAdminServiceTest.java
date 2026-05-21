package org.peepol.domain.service.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peepol.domain.enums.Role;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.peepol.domain.repo.UserRepo;
import org.peepol.dto.RegistrationDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthAdminServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthAdminService authAdminService;

    @Test
    void registerSuccess() {
        RegistrationDTO.Request request = new RegistrationDTO.Request("newuser", "Password123!", "USER");
        User user = User.builder()
                .id(1L)
                .username("newuser")
                .role(Role.USER)
                .build();
        user.setStatus(Status.ENABLED);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy("_system_");

        when(userRepo.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepo.save(any(User.class))).thenReturn(user);

        RegistrationDTO.Response result = authAdminService.register(request);

        assertNotNull(result);
        assertEquals("newuser", result.username());
        assertEquals("USER", result.role());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void registerUsernameNotAvailable() {
        RegistrationDTO.Request request = new RegistrationDTO.Request("existinguser", "Password123!", "USER");
        when(userRepo.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authAdminService.register(request));
    }

    @Test
    void registerInvalidRequest() {
        assertThrows(IllegalArgumentException.class, () -> authAdminService.register(null));
    }
}
