package org.peepol.controller;

import org.peepol.domain.service.AuthService;
import org.peepol.dto.LoginDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginDTO.Response> login(@Valid @RequestBody LoginDTO.Request request) {

        if (Objects.isNull(request)) throw new IllegalArgumentException();
        return ResponseEntity.ok(authService.login(
                request.username().toLowerCase(),
                request.password().toLowerCase()
        ));
    }
}
