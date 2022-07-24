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
import org.springframework.web.multipart.MultipartFile;
import ru.neonzoff.guidevologdamvc.dao.BaseEntityRepository;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Image;
import ru.neonzoff.guidevologdamvc.domain.Tag;
import ru.neonzoff.guidevologdamvc.domain.Track;
import ru.neonzoff.guidevologdamvc.domain.TypeEntity;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityDto;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CREATE_ENTITY;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertBaseEntitiesToList;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertBaseEntitiesToListDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertBaseEntityToDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertContactsDtoToList;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertPropertiesDtoToList;

/**
 * @author Tseplyaev Dmitry
 */
@Service
public class BaseEntityServiceImpl implements BaseEntityService {
    private final BaseEntityRepository repository;

    private final TypeEntityService typeEntityService;

    private final StreetService streetService;

    private final ImageService imageService;

    private final TagService tagService;

    private final ContactService contactService;

    private final AuditService auditService;

    private final GeoCoderService geoCoderService;

    public BaseEntityServiceImpl(BaseEntityRepository repository, TypeEntityService typeEntityService,
                                 StreetService streetService, ImageService imageService,
                                 TagService tagService, ContactService contactService,
                                 AuditService auditService, GeoCoderService geoCoderService) {
        this.repository = repository;
        this.typeEntityService = typeEntityService;
        this.streetService = streetService;
        this.imageService = imageService;
        this.tagService = tagService;
        this.contactService = contactService;
        this.auditService = auditService;
        this.geoCoderService = geoCoderService;
    }

    @Override
    public List<BaseEntityDto> findAll() {
        return convertBaseEntitiesToListDto(repository.findAll());
    }

    @Override
    public List<BaseEntityDto> findAll(String sortColumn) {
        return convertBaseEntitiesToListDto(repository.findAll(Sort.by(sortColumn).ascending()));
    }

    @Override
    public List<BaseEntityDto> findByActive(Boolean active) {
        return convertBaseEntitiesToListDto(repository.findByActive(active));
    }

    @Override
    public List<BaseEntityDto> findByTag(Tag tag) {
        return convertBaseEntitiesToListDto(repository.findByTagsContains(tag));
    }

    @Override
    public List<BaseEntityDto> findByTypeEntity(TypeEntity typeEntity) {
        return convertBaseEntitiesToListDto(repository.findByTypeEntity(typeEntity));
    }

    @Override
    public BaseEntityDto findByName(String name) {
//        обработать если не найден
        return convertBaseEntityToDto(repository.findByName(name).get());
    }

    @Override
    public BaseEntity findById(Long id) {
//        ifPresent
        return repository.findById(id).get();
    }

    @Override
    @Transactional
    public boolean addImagesToEntity(Long id, List<MultipartFile> images) {
        BaseEntity baseEntity = findById(id);
        List<Image> oldImages = baseEntity.getImages();
        baseEntity.setImages(imageService.saveImages(images));
        imageService.deleteImages(oldImages);
        repository.save(baseEntity);
        return true;
    }

    @Override
    public boolean saveBaseEntity(BaseEntityDto baseEntityDto) {
        BaseEntity baseEntity = new BaseEntity();
        if (baseEntityDto.getId() != null) {
            baseEntity = repository.getById(baseEntityDto.getId());
            if (baseEntityDto.getTypeEntity() != null)
                baseEntity.setTypeEntity(typeEntityService.findById(baseEntityDto.getTypeEntity()));
            baseEntity.setName(baseEntityDto.getName());
            baseEntity.setNameEn(baseEntityDto.getNameEn());
            baseEntity.setDescription(baseEntityDto.getDescription());
            baseEntity.setDescriptionEn(baseEntityDto.getDescriptionEn());
            if (baseEntityDto.getStreet() != null && !baseEntityDto.getStreet().toString().isEmpty()) {
                baseEntity.setStreet(streetService.findById(baseEntityDto.getStreet()));
            }
        } else {
            if (baseEntityDto.getTypeEntity() != null) {
                baseEntity.setTypeEntity(typeEntityService.findById(baseEntityDto.getTypeEntity()));
            }
            baseEntity.setName(baseEntityDto.getName());
            baseEntity.setNameEn(baseEntityDto.getNameEn());
            baseEntity.setDescription(baseEntityDto.getDescription());
            baseEntity.setDescriptionEn(baseEntityDto.getDescriptionEn());
            if (baseEntityDto.getStreet() != null) {
                baseEntity.setStreet(streetService.findById(baseEntityDto.getStreet()));
            }
        }
        baseEntity.setHouseNumber(baseEntityDto.getHouseNumber());
        if (baseEntity.getStreet() != null) {
            baseEntity.setPos(geoCoderService.getPos(streetService.findById(baseEntity.getStreet().getId()), baseEntityDto.getHouseNumber()));
        }
        if (baseEntityDto.getContacts() != null) {
            baseEntity.setContacts(convertContactsDtoToList(baseEntityDto.getContacts()));
        }
        if (baseEntityDto.getProperties() != null) {
            baseEntity.setProperties(convertPropertiesDtoToList(baseEntityDto.getProperties()));
        }
        baseEntity.setWorkSchedule(baseEntityDto.getWorkSchedule());
        baseEntity.setWorkScheduleEn(baseEntityDto.getWorkScheduleEn());
        if (baseEntityDto.getTags() != null && !baseEntityDto.getTags().isEmpty()) {
//            удаляем старые
            for (Tag tag : new ArrayList<>(baseEntity.getTags())) {
                baseEntity.removeTag(tag);
            }
//            добавляем новые
            for (Tag tag : tagService.findById(baseEntityDto.getTags()))
                baseEntity.setTag(tag);
        }
        baseEntity.setActive(baseEntityDto.getActive());
        repository.save(baseEntity);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(CREATE_ENTITY.getAction(), baseEntity.getId()),
                new Date()
        );
        return true;
    }

    @Override
    public boolean updateBaseEntity(BaseEntityDto baseEntityDto) {
        return false;
    }

    @Override
    public boolean removeTrack(BaseEntityDto baseEntityDto, Track track) {
        Optional<BaseEntity> baseEntity = repository.findById(baseEntityDto.getId());
        if (baseEntity.isPresent() && baseEntity.get().getTracks().contains(track)) {
            baseEntity.get().removeTrack(track);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean deleteBaseEntity(BaseEntityDto baseEntityDto) {
        Optional<BaseEntity> baseEntity = repository.findById(baseEntityDto.getId());
        if (baseEntity.isPresent() && baseEntity.get().getTracks().isEmpty()) {
            contactService.deleteContacts(baseEntity.get().getContacts());
            repository.delete(baseEntity.get());
            imageService.deleteImages(baseEntity.get().getImages());
            return true;
        }
        return false;
    }

    @Override
    public Page<BaseEntityModel> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<BaseEntity> entities = repository.findAll(pageable.getSort());
        List<BaseEntityModel> entityModels;

        if (entities.size() < startItem) {
            entityModels = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, entities.size());
            entityModels = convertBaseEntitiesToList(entities.subList(startItem, toIndex));
        }

        return new PageImpl<>(entityModels, PageRequest.of(currentPage, pageSize), entities.size());
    }
}
