package org.peepol.controller.auth;

import org.peepol.domain.service.admin.AuthAdminService;
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

    @PostMapping("/registration")
    public ResponseEntity<RegistrationDTO.Response> register(@Valid @RequestBody RegistrationDTO.Request request) {

        if (Objects.isNull(request)) throw new IllegalArgumentException();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authAdminService.register(request));
    }
}
