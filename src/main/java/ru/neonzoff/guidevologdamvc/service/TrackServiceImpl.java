package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neonzoff.guidevologdamvc.dao.TrackRepository;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Track;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.TrackDto;
import ru.neonzoff.guidevologdamvc.dto.TrackModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.lang.Long.parseLong;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CREATE_TRACK;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.DELETE_TRACK;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertTracksToListDto;

@Service
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;

    private final ImageService imageService;

    private final BaseEntityService baseEntityService;

    private final AuditService auditService;

    public TrackServiceImpl(TrackRepository trackRepository, ImageService imageService,
                            BaseEntityService baseEntityService, AuditService auditService) {
        this.trackRepository = trackRepository;
        this.imageService = imageService;
        this.baseEntityService = baseEntityService;
        this.auditService = auditService;
    }

    @Override
    public List<TrackDto> findAll() {
        return convertTracksToListDto(trackRepository.findAll());
    }

    @Override
    public List<TrackDto> findAll(String sortColumn) {
        return convertTracksToListDto(trackRepository.findAll(Sort.by(sortColumn)));
    }

    @Override
    public Track findByName(String name) {
        return trackRepository.findByName(name);
    }

    @Override
    public Track findById(Long id) {
        return trackRepository.findById(id).get();
    }

    @Override
    public boolean saveTrack(TrackModel trackModel) {
        if (trackRepository.findByName(trackModel.getName()) == null) {
            Track track = new Track();
            track.setName(trackModel.getName());
            track.setNameEn(trackModel.getNameEn());
            track.setDescription(trackModel.getDescription());
            track.setDescriptionEn(trackModel.getDescriptionEn());
            track.setImage(imageService.saveImage(trackModel.getImage()));
            if (trackModel.getEntities().length() > 1) {
                for (String entityId : trackModel.getEntities().split(","))
                    track.addEntity(baseEntityService.findById(parseLong(entityId)));
            } else if (trackModel.getEntities().length() == 1) {
                track.addEntity(baseEntityService.findById(parseLong(trackModel.getEntities())));
            }
            trackRepository.save(track);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            auditService.saveAction(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    String.format(CREATE_TRACK.getAction(), track.getId()),
                    new Date()
            );
        }
        return false;
    }

    @Override
    public boolean updateTrack(TrackModel trackModel) {
        return false;
    }

    @Transactional
    @Override
    public boolean deleteTrack(Track track) {
        List<BaseEntity> entities = track.getEntities();
        List<BaseEntity> tempEntities = new ArrayList<>(entities);
        int count = entities.size();
        for (int i = 0; i < count; i++) {
            tempEntities.get(i).removeTrack(track);
        }
        trackRepository.delete(track);
        imageService.deleteImage(track.getImage());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(DELETE_TRACK.getAction(), track.getId()),
                new Date()
        );
        return true;
    }

    @Override
    public Page<TrackDto> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Track> tracks = trackRepository.findAll(pageable.getSort());
        List<TrackDto> tracksDto;

        if (tracks.size() < startItem) {
            tracksDto = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, tracks.size());
            tracksDto = convertTracksToListDto(tracks.subList(startItem, toIndex));
        }

        return new PageImpl<>(tracksDto, PageRequest.of(currentPage, pageSize), tracks.size());
    }
}
