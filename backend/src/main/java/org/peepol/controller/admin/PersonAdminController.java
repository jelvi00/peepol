package org.peepol.controller.admin;

import org.peepol.controller.PersonController;
import org.peepol.domain.service.PersonService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/persons")
public class PersonAdminController extends PersonController {

    public PersonAdminController(PersonService personService) {
        super(personService);
    }

}
