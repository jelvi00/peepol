package org.peepol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PersonDTO {

    public record AddRequest(
            @NotBlank @Size(min = 3, max = 60) String name,
            @NotBlank @Size(min = 7, max = 20) String phoneNumber,
            @Size(max = 500) String bio
    ) {}

    public record UpdateRequest(
            @NotBlank Long id,
            @Size(min = 3, max = 60) String name,
            @Size(min = 7, max = 20) String phoneNumber,
            @Size(max = 500) String bio
    ) {}

    public record Response(
            Long id,
            String name,
            String phoneNumber,
            String bio,
            String status
    ) {}

}
