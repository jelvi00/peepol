package org.peepol.domain.repo;


import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.peepol.domain.enums.Status;
import org.peepol.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Collection;
import java.util.Optional;


public interface PersonRepo extends JpaRepository<Person, Long> {

    Person findByPhoneNumber(String phoneNumber);

    Page<Person> findAllByStatusIn(Collection<Status> status, Pageable pageable);

    @Query("SELECT p FROM Person p WHERE p.status IN :statuses AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Person> searchWithStatus(Collection<Status> statuses, String query, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Person> findForUpdateById(Long id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Person p WHERE p.phoneNumber = :phoneNumber AND (:id IS NULL OR p.id != :id)")
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

}

