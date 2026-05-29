package org.peepol.controller.person;

import org.peepol.domain.service.PersonService;
import org.peepol.mapper.PersonMapper;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/persons")
public final class PersonUserController extends PersonController {

    public PersonUserController(
            PersonService personService,
            PersonMapper personMapper
    ) {
        super(personService, personMapper);
    }

}
