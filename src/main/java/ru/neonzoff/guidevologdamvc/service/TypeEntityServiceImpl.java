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
import ru.neonzoff.guidevologdamvc.dao.TypeEntityRepository;
import ru.neonzoff.guidevologdamvc.domain.TypeEntity;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.TypeEntityDto;
import ru.neonzoff.guidevologdamvc.dto.TypeEntityModel;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CREATE_TYPE_ENTITY;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.DELETE_TYPE_ENTITY;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.INIT_TYPE_ENTITY;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.UPDATE_TYPE_ENTITY;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_ID;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_USERNAME;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertFileToMultipartFile;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertTypeEntitiesToListDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertTypeEntityToDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.getFileFromURL;

/**
 * @author Tseplyaev Dmitry
 */
@Service
public class TypeEntityServiceImpl implements TypeEntityService {
    private final TypeEntityRepository repository;

    private final ImageService imageService;

    private final AuditService auditService;

    public TypeEntityServiceImpl(TypeEntityRepository repository, ImageService imageService, AuditService auditService) {
        this.repository = repository;
        this.imageService = imageService;
        this.auditService = auditService;
    }

    @Override
    public List<TypeEntityDto> findAll() {
        return convertTypeEntitiesToListDto(repository.findAll());
    }

    @Override
    public List<TypeEntityDto> findAll(String sortColumn) {
        return convertTypeEntitiesToListDto(repository.findAll(Sort.by(sortColumn).ascending()));
    }

    @Override
    public TypeEntityDto findByName(String name) {
//        обработать если не найден
        return convertTypeEntityToDto(repository.findByName(name).get());
    }

    @Override
    public TypeEntity findById(Long id) {
//        ifPresent
        return repository.findById(id).get();
    }


    @Override
    @Transactional
    public boolean saveTypeEntity(TypeEntityModel typeEntityModel) {
        TypeEntity typeEntity = new TypeEntity();
        typeEntity.setName(typeEntityModel.getName());
        typeEntity.setNameEn(typeEntityModel.getNameEn());
        typeEntity.setDescription(typeEntityModel.getDescription());
        typeEntity.setDescriptionEn(typeEntityModel.getDescriptionEn());
        typeEntity.setImage(imageService.saveImage(typeEntityModel.getImage()));
        if (repository.findByName(typeEntityModel.getName()).isPresent())
            return false;
        repository.save(typeEntity);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(CREATE_TYPE_ENTITY.getAction(), typeEntity.getId()),
                new Date()
        );

        return true;
    }

    @Override
    @Transactional
    public boolean updateTypeEntity(TypeEntityDto typeEntityDto) {
        TypeEntity typeEntity = repository.getById(typeEntityDto.getId());
        if (typeEntity.getName().equals(typeEntityDto.getName()) && typeEntity.getNameEn().equals(typeEntityDto.getNameEn()))
            return false;
//                todo: а сохранился ли он измененный? Проверить.
//        для обоих меняем
        typeEntity.setName(typeEntityDto.getName());
        typeEntity.setNameEn(typeEntityDto.getNameEn());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(UPDATE_TYPE_ENTITY.getAction(), typeEntity.getId()),
                new Date());

        return true;
    }

    @Override
    @Transactional
    public boolean deleteTypeEntity(TypeEntityDto typeEntityDto) {
        Optional<TypeEntity> typeEntity = repository.findById(typeEntityDto.getId());
        if (typeEntity.isPresent() && typeEntity.get().getEntities().isEmpty()) {
            repository.delete(typeEntity.get());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            auditService.saveAction(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    String.format(DELETE_TYPE_ENTITY.getAction(), typeEntity.get().getId()),
                    new Date()
            );
            return true;
        }
        return false;
    }

    @Override
    public Page<TypeEntityDto> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<TypeEntity> typeEntities = repository.findAll(pageable.getSort());
        List<TypeEntityDto> typeEntitiesDto;

        if (typeEntities.size() < startItem) {
            typeEntitiesDto = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, typeEntities.size());
            typeEntitiesDto = convertTypeEntitiesToListDto(typeEntities.subList(startItem, toIndex));
        }

        return new PageImpl<>(typeEntitiesDto, PageRequest.of(currentPage, pageSize), typeEntities.size());
    }

    @Transactional
    public void createDefaultTypes() throws IOException {
        if (repository.findAll().isEmpty()) {
            TypeEntity typeCafe = new TypeEntity();
            typeCafe.setName("Кафе");
            typeCafe.setNameEn("Cafe");
            typeCafe.setDescription("Тестовое описание типа кафе");
            typeCafe.setDescriptionEn("Тестовое описание типа кафе EN");
            typeCafe.setImage(imageService.saveImage(convertFileToMultipartFile(
                    getFileFromURL(
                            "https://storage.yandexcloud.net/guidevologda/hotel_icon.png",
                            "",
                            "hotel",
                            ".png"
                    ))
            ));
            repository.save(typeCafe);

            TypeEntity typeHotel = new TypeEntity();
            typeHotel.setName("Отель");
            typeHotel.setNameEn("Hotel");
            typeHotel.setDescription("Тестовое описание типа отель");
            typeHotel.setDescriptionEn("Тестовое описание типа отель EN");
            typeHotel.setImage(imageService.saveImage(convertFileToMultipartFile(
                    getFileFromURL(
                            "https://storage.yandexcloud.net/guidevologda/cafe_icon.png",
                            "",
                            "cafe",
                            ".png"
                    ))
            ));
            repository.save(typeHotel);

            auditService.saveAction(
                    SYSTEM_ID,
                    SYSTEM_USERNAME,
                    String.format(INIT_TYPE_ENTITY.getAction(), typeCafe.getId() + "," + typeHotel.getId()),
                    new Date()
            );
        }
    }
}
