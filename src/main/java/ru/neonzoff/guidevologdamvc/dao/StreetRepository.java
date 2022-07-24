package ru.neonzoff.guidevologdamvc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neonzoff.guidevologdamvc.domain.Street;


import java.util.Optional;

/**
 * @author Tseplyaev Dmitry
 */
@Repository
public interface StreetRepository extends JpaRepository<Street, Long> {

    Optional<Street> findByName(String street);
}
