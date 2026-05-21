package org.peepol.domain.service.admin;

import lombok.RequiredArgsConstructor;
import org.peepol.domain.enums.Role;
import org.peepol.domain.model.User;
import org.peepol.domain.repo.UserRepo;
import org.peepol.dto.RegistrationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthAdminService {

    private static final Logger logger = LoggerFactory.getLogger(AuthAdminService.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegistrationDTO.Response register(RegistrationDTO.Request request) {

        if (Objects.isNull(request)
                || Objects.isNull(request.username()) || request.username().isBlank()
                || Objects.isNull(request.password()) || request.password().isBlank()
        ) throw new IllegalArgumentException("Invalid registration.");

        if (userRepo.existsByUsername(request.username()))
            throw new IllegalArgumentException("Username is not available.");

        var user = userRepo.save(
                User.builder()
                        .username(request.username())
                        .password(passwordEncoder.encode(request.password()))
                        .role(Objects.nonNull(request.role()) ? Role.valueOf(request.role()) : Role.USER)
                        .build()
        );

        logger.info("Registration succeed for user: [{}]", user.getUsername());

        return new RegistrationDTO.Response(
                user.getId(),
                user.getUsername(),
                user.getRole().toString(),
                user.getStatus().toString(),
                user.getCreatedBy(),
                user.getCreatedAt().toString()
        );
    }

}
