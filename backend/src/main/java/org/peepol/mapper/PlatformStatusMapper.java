package org.peepol.mapper;

import org.mapstruct.Mapper;
import org.peepol.client.response.WebAppHealthResponse;
import org.peepol.dto.PlatformStatusDTO;

@Mapper(componentModel = "spring")
public interface PlatformStatusMapper {

    PlatformStatusDTO.Response toResponse(WebAppHealthResponse web, PlatformStatusDTO.APIHealth api);
}
