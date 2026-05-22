package org.peepol.domain.repo;

import org.peepol.domain.enums.Status;
import org.peepol.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndStatus(String username, Status status);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}
