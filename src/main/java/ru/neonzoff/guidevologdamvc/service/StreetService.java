package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.neonzoff.guidevologdamvc.domain.Street;
import ru.neonzoff.guidevologdamvc.dto.StreetDto;

import java.util.List;

/**
 * Класс для работы с репозиторием улиц
 *
 * @author Tseplyaev Dmitry
 */
public interface StreetService {
    List<StreetDto> findAll();

    List<StreetDto> findAll(String sortColumn);

    StreetDto findByName(String name);

    Street findById(Long id);

    boolean saveStreet(StreetDto streetDto);

    boolean updateStreet(StreetDto streetDto);

    boolean deleteStreet(StreetDto streetDto);

    Page<StreetDto> findPaginated(Pageable pageable);
}
