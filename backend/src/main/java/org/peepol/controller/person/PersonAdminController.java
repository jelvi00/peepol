package org.peepol.controller.person;

import org.peepol.domain.service.PersonService;
import org.peepol.dto.PersonDTO;
import org.peepol.mapper.PersonMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/persons")
public final class PersonAdminController extends PersonController {

    public PersonAdminController(
            PersonService personService,
            PersonMapper personMapper
    ) {
        super(personService, personMapper);
    }

    @DeleteMapping("/{id}/person")
    public ResponseEntity<PersonDTO.Response> removePerson(@PathVariable Long id) {
        var person = personService.removePerson(id);
        return ResponseEntity.ok(personMapper.toResponse(person));
    }

}
