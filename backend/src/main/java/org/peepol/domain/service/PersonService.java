package org.peepol.domain.service;

import org.peepol.domain.model.Person;
import org.peepol.dto.PersonDTO;

import java.util.List;

public interface PersonService {

    List<Person> getAllPersons(String status, int page, int size);
    List<Person> searchPersons(String query, String status, int page, int size);
    Person getPerson(Long id);
    Person addPerson(PersonDTO.AddRequest request);
    Person updatePerson(PersonDTO.UpdateRequest request);
    Person removePerson(Long id);

}
