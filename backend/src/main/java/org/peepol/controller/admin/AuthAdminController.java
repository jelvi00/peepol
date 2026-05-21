package org.peepol.controller.admin;

import org.peepol.domain.enums.Role;
import org.peepol.domain.service.AuthService;
import org.peepol.domain.service.admin.AuthAdminService;
import org.peepol.dto.LoginDTO;
import org.peepol.dto.RegistrationDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {

    private final AuthAdminService authAdminService;
    private final AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationDTO.Response> register(@Valid @RequestBody RegistrationDTO.Request request) {

        if (Objects.isNull(request)) throw new IllegalArgumentException();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authAdminService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO.Response> login(@Valid @RequestBody LoginDTO.Request request) {

        if (Objects.isNull(request)) throw new IllegalArgumentException();
        return ResponseEntity.ok(authService.login(request.username(), request.password(), Role.ADMIN));
    }
}
