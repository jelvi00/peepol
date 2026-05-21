package org.peepol.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    public record Request(
            @NotBlank String username,
            @NotBlank String password
    ) {
        public Request(String username, String password) {
            if (username.contains(" ")) throw new IllegalArgumentException("Username cannot contain spaces.");
            if (password.contains(" ")) throw new IllegalArgumentException("Password cannot contain spaces.");

            this.username = username;
            this.password = password;
        }
    }

    public record Response(
            String token,
            Long id,
            String username,
            String role
    ) {}
}
