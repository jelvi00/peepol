package org.peepol.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.peepol.domain.service.PersonService;
import org.peepol.dto.PersonDTO;
import org.peepol.mapper.PersonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(value = "status", defaultValue = "1") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        var persons = personService.getAllPersons(status, page, size);
        return ResponseEntity.ok(persons.stream().map(personMapper::toResponse).toList());
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDTO.Response>> searchPersons(
            @RequestParam("q") String query,
            @RequestParam(value = "status", defaultValue = "1") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        var persons = personService.searchPersons(query, status, page, size);
        return ResponseEntity.ok(persons.stream().map(personMapper::toResponse).toList());
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<PersonDTO.Response> getPerson(@PathVariable Long id) {
        var person = personService.getPerson(id);

        if (Objects.isNull(person)) return ResponseEntity.ok().build();
        return ResponseEntity.ok(personMapper.toResponse(person));
    }

    @PostMapping
    public ResponseEntity<PersonDTO.Response> addPerson(@Valid @RequestBody PersonDTO.AddRequest request) {
        var person = personService.addPerson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(personMapper.toResponse(person));
    }

    @PutMapping
    public ResponseEntity<PersonDTO.Response> updatePerson(@Valid @RequestBody PersonDTO.UpdateRequest request) {
        var person = personService.updatePerson(request);
        return ResponseEntity.ok(personMapper.toResponse(person));
    }

    @DeleteMapping("/{id}/person")
    public ResponseEntity<PersonDTO.Response> removePerson(@PathVariable Long id) {
        var person = personService.removePerson(id);
        return ResponseEntity.ok(personMapper.toResponse(person));
    }

}
