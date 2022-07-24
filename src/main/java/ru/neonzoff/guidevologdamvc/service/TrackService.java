package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.neonzoff.guidevologdamvc.domain.Track;
import ru.neonzoff.guidevologdamvc.dto.TrackDto;
import ru.neonzoff.guidevologdamvc.dto.TrackModel;

import java.util.List;

/**
 * Класс для работы с репозиторием треков
 *
 * @author Tseplyaev Dmitry
 */
public interface TrackService {
    List<TrackDto> findAll();

    List<TrackDto> findAll(String sortColumn);

    Track findByName(String name);

    Track findById(Long id);

    boolean saveTrack(TrackModel trackModel);

    boolean updateTrack(TrackModel trackModel);

    boolean deleteTrack(Track track);

    Page<TrackDto> findPaginated(Pageable pageable);
}
