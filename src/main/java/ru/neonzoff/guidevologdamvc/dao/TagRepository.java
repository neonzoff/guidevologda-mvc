package ru.neonzoff.guidevologdamvc.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neonzoff.guidevologdamvc.domain.Tag;

import java.util.Optional;

/**
 * @author Tseplyaev Dmitry
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
