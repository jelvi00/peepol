package org.peepol.controller.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peepol.client.WebAppService;
import org.peepol.controller.PlatformStatusAdminController;
import org.peepol.dto.PlatformStatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatformStatusAdminControllerTest {

    @Mock
    private WebAppService webAppService;

    @InjectMocks
    private PlatformStatusAdminController platformStatusAdminController;

    @Test
    void getStatusSuccess() {
        // Given
        PlatformStatusDTO.WebAppHealth webResponse = new PlatformStatusDTO.WebAppHealth("UP", "Web is running");

        when(webAppService.health()).thenReturn(webResponse);

        // When
        ResponseEntity<PlatformStatusDTO.Response> responseEntity = platformStatusAdminController.getStatus();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("UP", responseEntity.getBody().web().status());
        assertEquals("OK", responseEntity.getBody().api().status());
        verify(webAppService).health();
    }

    @Test
    void getStatusWhenWebAppServiceFails() {
        // Given
        when(webAppService.health()).thenThrow(new RuntimeException("Service down"));

        // When
        ResponseEntity<PlatformStatusDTO.Response> responseEntity = platformStatusAdminController.getStatus();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("DOWN", responseEntity.getBody().web().status());
        assertEquals("OK", responseEntity.getBody().api().status());
        verify(webAppService).health();
    }
}
