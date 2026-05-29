package org.peepol.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.Person;
import org.peepol.domain.repo.PersonRepo;
import org.peepol.dto.PersonDTO;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepo personRepo;

    @InjectMocks
    private PersonServiceImpl personService;

    private Person person;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .id(1L)
                .name("John Doe")
                .phoneNumber("123456789")
                .build();
        person.setStatus(Status.ENABLED);
    }

    @Test
    void getAllPersons() {
        when(personRepo.findAllByStatusIn(anySet(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(person)));

        List<Person> result = personService.getAllPersons("1", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personRepo).findAllByStatusIn(anySet(), any(PageRequest.class));
    }

    @Test
    void searchPersons() {
        when(personRepo.searchWithStatus(anySet(), anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(person)));

        List<Person> result = personService.searchPersons("John", "1", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personRepo).searchWithStatus(anySet(), eq("John"), any(PageRequest.class));
    }

    @Test
    void getPerson() {
        when(personRepo.findById(1L)).thenReturn(Optional.of(person));

        Person result = personService.getPerson(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getPersonNotFound() {
        when(personRepo.findById(1L)).thenReturn(Optional.empty());

        Person result = personService.getPerson(1L);

        assertNull(result);
    }

    @Test
    void addPersonSuccess() {
        PersonDTO.AddRequest request = new PersonDTO.AddRequest("Jane Doe", "987654321", "Bio");
        when(personRepo.existsByPhoneNumberAndIdNot(anyString(), any())).thenReturn(false);
        when(personRepo.save(any(Person.class))).thenReturn(person);

        Person result = personService.addPerson(request);

        assertNotNull(result);
        verify(personRepo).save(any(Person.class));
    }

    @Test
    void addPersonAlreadyExists() {
        PersonDTO.AddRequest request = new PersonDTO.AddRequest("Jane Doe", "123456789", "Bio");
        when(personRepo.existsByPhoneNumberAndIdNot(anyString(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> personService.addPerson(request));
    }

    @Test
    void updatePersonSuccess() {
        PersonDTO.UpdateRequest request = new PersonDTO.UpdateRequest(1L, "John Updated", "123456789", "New Bio");
        when(personRepo.findForUpdateById(1L)).thenReturn(Optional.of(person));
        when(personRepo.existsByPhoneNumberAndIdNot(anyString(), any())).thenReturn(false);
        when(personRepo.save(any(Person.class))).thenReturn(person);

        Person result = personService.updatePerson(request);

        assertNotNull(result);
        assertEquals("John Updated", person.getName());
        verify(personRepo).save(person);
    }

    @Test
    void updatePersonNotFound() {
        PersonDTO.UpdateRequest request = new PersonDTO.UpdateRequest(1L, "John Updated", "123456789", "New Bio");
        when(personRepo.findForUpdateById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> personService.updatePerson(request));
    }

    @Test
    void updatePersonDisabled() {
        person.setStatus(Status.DISABLED);
        PersonDTO.UpdateRequest request = new PersonDTO.UpdateRequest(1L, "John Updated", "123456789", "New Bio");
        when(personRepo.findForUpdateById(1L)).thenReturn(Optional.of(person));

        assertThrows(IllegalArgumentException.class, () -> personService.updatePerson(request));
    }

    @Test
    void removePersonSuccess() {
        when(personRepo.findById(1L)).thenReturn(Optional.of(person));
        when(personRepo.save(any(Person.class))).thenReturn(person);

        Person result = personService.removePerson(1L);

        assertNotNull(result);
        assertEquals(Status.DISABLED, person.getStatus());
        verify(personRepo).save(person);
    }

    @Test
    void removePersonNotFound() {
        when(personRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> personService.removePerson(1L));
    }

    @Test
    void removePersonAlreadyDisabled() {
        person.setStatus(Status.DISABLED);
        when(personRepo.findById(1L)).thenReturn(Optional.of(person));

        assertThrows(IllegalArgumentException.class, () -> personService.removePerson(1L));
    }
}
