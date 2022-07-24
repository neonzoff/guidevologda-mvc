package ru.neonzoff.guidevologdamvc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Tag;
import ru.neonzoff.guidevologdamvc.domain.TypeEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Tseplyaev Dmitry
 */
@Repository
public interface BaseEntityRepository extends JpaRepository<BaseEntity, Long> {

    Optional<BaseEntity> findByName(String name);

    List<BaseEntity> findByActive(Boolean active);

    List<BaseEntity> findByTagsContains(Tag tag);

    List<BaseEntity> findByTypeEntity(TypeEntity typeEntity);
}
