package org.peepol.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.peepol.domain.service.PersonService;
import org.peepol.dto.PersonDTO;
import org.peepol.mapper.PersonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/admin/persons")
@RequiredArgsConstructor
public class PersonAdminController {

    private final PersonService personService;
    private final PersonMapper personMapper;

    @GetMapping()
    public ResponseEntity<List<PersonDTO.Response>> getPersons(
            @Nullable @RequestParam("status") String status,
            @Nullable @RequestParam("page") Integer page,
            @Nullable @RequestParam("size") Integer size
    ) {

        var persons = personService.getAllPersons(
                status,
                Objects.nonNull(page) ? page : 0,
                Objects.nonNull(size) ? size : 10
        );

        return ResponseEntity.ok(persons.isEmpty()
                ? Collections.emptyList()
                : persons.stream().map(personMapper::toResponse).toList()
        );

    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDTO.Response>> searchPersons(
            @RequestParam("q") String query,
            @Nullable @RequestParam("status") String status,
            @Nullable @RequestParam("page") Integer page,
            @Nullable @RequestParam("size") Integer size
    ) {
        var persons = personService.searchPersons(
                query,
                status,
                Objects.nonNull(page) ? page : 0,
                Objects.nonNull(size) ? size : 10
        );

        return ResponseEntity.ok(persons.isEmpty()
                ? Collections.emptyList()
                : persons.stream().map(personMapper::toResponse).toList()
        );
    }



    @GetMapping("/{id}/detail")
    public ResponseEntity<PersonDTO.Response> getPerson(@PathVariable String id) {

        var person = personService.getPerson(Long.valueOf(id));

        if (Objects.isNull(person)) return ResponseEntity.ok().build();
        else return ResponseEntity.ok(personMapper.toResponse(person));

    }

    @PostMapping
    public ResponseEntity<PersonDTO.Response> addPerson(@Valid @RequestBody PersonDTO.AddRequest request) {

        var person = personService.addPerson(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        Objects.isNull(person)
                                ? null
                                : personMapper.toResponse(person)
                );
    }

    @PutMapping
    public ResponseEntity<PersonDTO.Response> updatePerson(@Valid @RequestBody PersonDTO.UpdateRequest request) {

        var person = personService.updatePerson(request);

        return ResponseEntity.ok(Objects.isNull(person)
                ? null
                : personMapper.toResponse(person)
        );
    }

    @DeleteMapping("/{id}/person")
    public ResponseEntity<PersonDTO.Response> removePerson(@PathVariable String id) {

        var person = personService.removePerson(Long.valueOf(id));

        return ResponseEntity.ok(Objects.isNull(person)
                ? null
                : personMapper.toResponse(person)
        );
    }

}
