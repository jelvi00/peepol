package org.peepol.controller.admin;

import lombok.RequiredArgsConstructor;
import org.peepol.client.WebAppService;
import org.peepol.dto.PlatformStatusDTO;
import org.peepol.mapper.PlatformStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    private final PlatformStatusMapper platformStatusMapper;

    @GetMapping()

    ResponseEntity<PlatformStatusDTO.Response> getStatus() {

        PlatformStatusDTO.Response response;
        var apiHealth = new PlatformStatusDTO.APIHealth("OK", "API is available.");

        try {
            var webappHealthResponse = webAppService.health();

            response = platformStatusMapper.toResponse(webappHealthResponse, apiHealth);
        } catch (Exception e) {
            logger.warn("Unable to reach Peepol Web App.");
            response = platformStatusMapper.toResponse(null, apiHealth);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
