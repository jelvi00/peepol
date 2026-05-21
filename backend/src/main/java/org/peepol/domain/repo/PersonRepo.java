package org.peepol.domain.repo;


import org.peepol.domain.enums.Status;
import org.peepol.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;


public interface PersonRepo extends JpaRepository<Person, Long> {

    Person findByPhoneNumber(String phoneNumber);

    Page<Person> findAllByStatusIn(Collection<Status> status, Pageable pageable);

    Page<Person> findByNameContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(String name, String phoneNumber, Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);

}

