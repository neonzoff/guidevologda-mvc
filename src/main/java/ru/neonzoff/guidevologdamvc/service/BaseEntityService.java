package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Tag;
import ru.neonzoff.guidevologdamvc.domain.Track;
import ru.neonzoff.guidevologdamvc.domain.TypeEntity;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityDto;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityModel;

import java.util.List;

/**
 * Класс для работы с репозиторием сущностей
 *
 * @author Tseplyaev Dmitry
 */
public interface BaseEntityService {
    List<BaseEntityDto> findAll();

    List<BaseEntityDto> findAll(String sortColumn);

    List<BaseEntityDto> findByActive(Boolean active);

    List<BaseEntityDto> findByTag(Tag tag);

    List<BaseEntityDto> findByTypeEntity(TypeEntity typeEntity);

    BaseEntityDto findByName(String name);

    BaseEntity findById(Long id);

    boolean addImagesToEntity(Long id, List<MultipartFile> images);

    boolean saveBaseEntity(BaseEntityDto baseEntityDto);

    boolean updateBaseEntity(BaseEntityDto baseEntityDto);

    boolean deleteBaseEntity(BaseEntityDto baseEntityDto);

    boolean removeTrack(BaseEntityDto baseEntityDto, Track track);

    Page<BaseEntityModel> findPaginated(Pageable pageable);
}
