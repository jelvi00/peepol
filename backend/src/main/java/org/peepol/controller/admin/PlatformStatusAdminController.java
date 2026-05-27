package org.peepol.controller.admin;

import lombok.RequiredArgsConstructor;
import org.peepol.client.WebAppService;
import org.peepol.dto.PlatformStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/status")
@RequiredArgsConstructor
public class PlatformStatusAdminController {

    private static final Logger logger = LoggerFactory.getLogger(PlatformStatusAdminController.class);

    private final WebAppService webAppService;

    @GetMapping
    ResponseEntity<PlatformStatusDTO.Response> getStatus() {

        var apiHealth = new PlatformStatusDTO.APIHealth("OK", "API is available.");
        PlatformStatusDTO.WebAppHealth webappHealth;

        try {
            webappHealth = webAppService.health();
        } catch (Exception e) {
            webappHealth = new PlatformStatusDTO.WebAppHealth("DOWN", "Web App is unreachable.");
            logger.error("Error: ", e);
        }

        return ResponseEntity.ok(new PlatformStatusDTO.Response(webappHealth, apiHealth));
    }
}
