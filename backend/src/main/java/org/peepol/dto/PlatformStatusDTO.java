package org.peepol.dto;

public class PlatformStatusDTO {

    public record WebAppHealth(String status, String message) {}
    public record APIHealth(String status, String message) {}

    public record Response(
            WebAppHealth web,
            APIHealth api
    ) {}
}
