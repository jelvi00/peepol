package org.peepol.domain.service;

import org.peepol.domain.enums.Status;
import org.peepol.domain.model.Person;
import org.peepol.domain.repo.PersonRepo;
import org.peepol.dto.PersonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepo personRepo;

    public List<Person> getAllPersons(String status, int page, int size) {

        Set<Status> statuses = parseStatuses(status);

        return personRepo.findAllByStatusIn(
                statuses,
                PageRequest.of(page, size, Sort.by("status").descending())
        ).getContent();

    }

    public List<Person> searchPersons(String query, String status, int page, int size) {

        Set<Status> statuses = parseStatuses(status);

        return personRepo.searchWithStatus(
                statuses,
                query,
                PageRequest.of(page, size, Sort.by("name").ascending())
        ).getContent();
    }

    public Person getPerson(Long id) {

        return personRepo.findById(id).orElse(null);

    }

    public Person addPerson(PersonDTO.AddRequest request) {

        if (personRepo.existsByPhoneNumber(request.phoneNumber().toLowerCase()))
            throw new IllegalArgumentException("Phone Number is not available.");

        return personRepo.save(
                Person.builder()
                        .name(request.name())
                        .phoneNumber(request.phoneNumber().toLowerCase())
                        .bio(request.bio())
                        .build()
        );

    }

    public Person updatePerson(PersonDTO.UpdateRequest request) {

        if (Objects.isNull(request.name()) && Objects.isNull(request.phoneNumber()))
            throw new IllegalArgumentException("Nothing to do.");

        var byIdPerson = personRepo.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("Person not found."));

        if (byIdPerson.getStatus().equals(Status.DISABLED))
            throw new IllegalArgumentException("Person is already removed.");

        var byPhonePerson = Objects.nonNull(request.phoneNumber())
                ? personRepo.findByPhoneNumber(request.phoneNumber().toLowerCase())
                : null;

        if (Objects.nonNull(byPhonePerson)
                && !Objects.equals(byPhonePerson.getId().toString(), byIdPerson.getId().toString()))
            throw new IllegalArgumentException("Phone number is not available.");

        if (Objects.nonNull(request.name())) byIdPerson.setName(request.name());
        if (Objects.nonNull(request.phoneNumber())) byIdPerson.setPhoneNumber(request.phoneNumber().toLowerCase());
        if (Objects.nonNull(request.bio())) byIdPerson.setBio(request.bio().toLowerCase());

        return personRepo.save(byIdPerson);

    }

    public Person removePerson(Long id) {

        var person = personRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        if (person.getStatus().equals(Status.DISABLED))
            throw new IllegalArgumentException("Person is already removed.");

        person.setStatus(Status.DISABLED);

        return personRepo.save(person);
    }

    private Set<Status> parseStatuses(String statuses) {
        if (Objects.isNull(statuses) || statuses.isBlank()) statuses = "1";

        return statuses.contains(",")
                ? Arrays.stream(statuses.split(","))
                .map(s -> Status.fromId(Integer.parseInt(s.trim())))
                .collect(Collectors.toSet())
                : Collections.singleton(Status.fromId(Integer.parseInt(statuses)));
    }

}
