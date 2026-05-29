package org.peepol.domain.service;

import org.peepol.domain.enums.Status;
import org.peepol.domain.model.Person;
import org.peepol.domain.repo.PersonRepo;
import org.peepol.dto.PersonDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PersonServiceImpl.class);

    private final PersonRepo personRepo;

    public List<Person> getAllPersons(String status, int page, int size) {

        Set<Status> statuses = parseStatuses(status);

        return personRepo.findAllByStatusIn(
                statuses,
                PageRequest.of(page, size, Sort.by("id").ascending())
        ).getContent();

    }

    public List<Person> searchPersons(String query, String status, int page, int size) {

        Set<Status> statuses = parseStatuses(status);

        return personRepo.searchWithStatus(
                statuses,
                query,
                PageRequest.of(page, size, Sort.by("id").ascending())
        ).getContent();
    }

    public Person getPerson(Long id) {

        return personRepo.findById(id).orElse(null);

    }

    public Person addPerson(PersonDTO.AddRequest request) {
        validatePersonRequest(request);
        validatePhoneNumberAvailability(request.phoneNumber(), null);

        return personRepo.save(
                Person.builder()
                        .name(request.name())
                        .phoneNumber(request.phoneNumber())
                        .bio(request.bio())
                        .build()
        );

    }

    @Transactional
    public Person updatePerson(PersonDTO.UpdateRequest request) {
        validatePersonRequest(request);

        var byIdPerson = getPersonById(request.id());
        validatePersonStatus(byIdPerson);
        validatePhoneNumberAvailability(request.phoneNumber(), request.id());

        if (Objects.nonNull(request.name())) byIdPerson.setName(request.name());
        if (Objects.nonNull(request.phoneNumber())) byIdPerson.setPhoneNumber(request.phoneNumber());
        if (Objects.nonNull(request.bio())) byIdPerson.setBio(request.bio());

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
        return Arrays.stream(statuses.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> Status.fromId(Integer.parseInt(s)))
                .collect(Collectors.toSet());
    }

    private void validatePersonRequest(PersonDTO.Request request) {

        if (Objects.isNull(request.name()) && Objects.isNull(request.phoneNumber()) && Objects.isNull(request.bio()))
            throw new IllegalArgumentException("Nothing to do.");

    }

    private Person getPersonById(Long id) {

        Person byIdPerson;
        try {
            byIdPerson = personRepo.findForUpdateById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Person not found."));
        } catch (Exception e) {
            log.error("Unable to update person, try again later.");
            throw e;
        }

        return byIdPerson;
    }

    private void validatePersonStatus(Person person) {
        if (person.getStatus().equals(Status.DISABLED))
            throw new IllegalArgumentException("Person is already removed.");
    }

    private void validatePhoneNumberAvailability(String phoneNumber, Long id) {
        if (personRepo.existsByPhoneNumberAndIdNot(phoneNumber, id))
            throw new IllegalArgumentException("Phone number is not available.");
    }

}
