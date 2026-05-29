package org.peepol.domain.repo;

import jakarta.persistence.LockModeType;
import org.peepol.domain.enums.WishStatus;
import org.peepol.domain.model.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface WishRepo extends JpaRepository<Wish, Long> {

    Page<Wish> findByWishStatus(WishStatus wishStatus, Pageable pageable);

    Optional<Wish> findByDescriptionContainingIgnoreCase(String description);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Wish> findByNameContainingIgnoreCase(String name);

    List<Wish> findByPersonId(Long personId);

    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    Optional<Wish> findForUpdateById(Long id);

}
