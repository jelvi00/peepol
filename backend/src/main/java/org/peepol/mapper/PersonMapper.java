package org.peepol.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.peepol.domain.model.Person;
import org.peepol.dto.PersonDTO;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    @Mapping(target = "status", expression = "java(person.getStatus().name())")
    PersonDTO.Response toResponse(Person person);
}
