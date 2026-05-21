package org.peepol.dto;

import org.peepol.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegistrationDTO {

    public record Request(
            @NotBlank @Pattern(
                    regexp = "^(?!.*anonymousUser)(?!.*_system_)[A-Za-z]\\w{7,30}$",
                    message = "Username is invalid, should start with letter and be between 7 and 30 chars length"
            ) String username,
            @NotBlank @Pattern(
                    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
                    message = "Password is invalid, must contain at least one digit, one upper, one lower, one special character, and be at least 8 characters long"
            ) String password,
            @Pattern(
                    regexp = "^(USER|ADMIN)$", /** @see Role */
                    message = "Provided role is invalid."
            ) String role
    ) {}

    public record Response(
            Long id,
            String username,
            String role,
            String status,
            String createdBy,
            String createdDate
    ) {}
}
