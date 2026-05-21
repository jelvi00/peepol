package org.peepol.domain.repo;


import org.peepol.domain.enums.Status;
import org.peepol.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.Collection;


public interface PersonRepo extends JpaRepository<Person, Long> {

    Person findByPhoneNumber(String phoneNumber);

    Page<Person> findAllByStatusIn(Collection<Status> status, Pageable pageable);

    Page<Person> findByNameContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(String name, String phoneNumber, Pageable pageable);

    @Query("SELECT p FROM Person p WHERE p.status IN :statuses AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Person> searchWithStatus(Collection<Status> statuses, String query, Pageable pageable);

    boolean existsByPhoneNumber(String phoneNumber);

}

