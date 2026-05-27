package org.peepol.controller.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peepol.client.WebAppService;
import org.peepol.client.response.WebAppHealthResponse;
import org.peepol.dto.PlatformStatusDTO;
import org.peepol.mapper.PlatformStatusMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformStatusAdminControllerTest {

    @Mock
    private WebAppService webAppService;

    @Mock
    private PlatformStatusMapper platformStatusMapper;

    @InjectMocks
    private PlatformStatusAdminController platformStatusAdminController;

    @Test
    void getStatusSuccess() {
        // Given
        WebAppHealthResponse webResponse = new WebAppHealthResponse("UP", "Web is running");
        PlatformStatusDTO.Response expectedResponse = new PlatformStatusDTO.Response(
                new PlatformStatusDTO.WebAppHealth("UP", "Web is running"),
                new PlatformStatusDTO.APIHealth("OK", "API is available.")
        );

        when(webAppService.health()).thenReturn(webResponse);
        when(platformStatusMapper.toResponse(eq(webResponse), any(PlatformStatusDTO.APIHealth.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<PlatformStatusDTO.Response> responseEntity = platformStatusAdminController.getStatus();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("UP", responseEntity.getBody().web().status());
        verify(webAppService).health();
    }

    @Test
    void getStatusWhenWebAppServiceFails() {
        // Given
        PlatformStatusDTO.Response expectedResponse = new PlatformStatusDTO.Response(
                null,
                new PlatformStatusDTO.APIHealth("OK", "API is available.")
        );

        when(webAppService.health()).thenThrow(new RuntimeException("Service down"));
        when(platformStatusMapper.toResponse(eq(null), any(PlatformStatusDTO.APIHealth.class)))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<PlatformStatusDTO.Response> responseEntity = platformStatusAdminController.getStatus();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNull(responseEntity.getBody().web());
        verify(webAppService).health();
    }
}
