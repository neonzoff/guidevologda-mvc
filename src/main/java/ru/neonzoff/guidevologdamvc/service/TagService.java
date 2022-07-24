package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.neonzoff.guidevologdamvc.domain.Tag;
import ru.neonzoff.guidevologdamvc.dto.TagDto;

import java.util.List;

/**
 * Класс для работы с репозиторием тегов
 *
 * @author Tseplyaev Dmitry
 */
public interface TagService {
    List<TagDto> findAll();

    List<TagDto> findAll(String sortColumn);

    Tag findByName(String name);

    List<Tag> findById(List<Long> ids);

    Tag findById(Long id);

    boolean saveTag(TagDto tagDto);

    boolean updateTag(TagDto tagDto);

    boolean deleteTag(TagDto tagDto);

    Page<TagDto> findPaginated(Pageable pageable);

    void createDefaultTypes();
}
