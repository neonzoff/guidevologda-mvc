package ru.neonzoff.guidevologdamvc.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.neonzoff.guidevologdamvc.dao.HomeEntityRepository;
import ru.neonzoff.guidevologdamvc.domain.HomeEntity;
import ru.neonzoff.guidevologdamvc.domain.Image;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.HomeEntityDto;
import ru.neonzoff.guidevologdamvc.dto.HomeEntityModel;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CREATE_HOME_ENTITY;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.UPDATE_HOME_ENTITY;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_ID;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_USERNAME;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertFileToMultipartFile;
import static ru.neonzoff.guidevologdamvc.utils.Converter.getFileFromURL;

@Service
public class HomeEntityServiceImpl implements HomeEntityService {

    private final HomeEntityRepository homeEntityRepository;

    private final ImageService imageService;

    private final AuditService auditService;

    public HomeEntityServiceImpl(HomeEntityRepository homeEntityRepository, ImageService imageService,
                                 AuditService auditService) {
        this.homeEntityRepository = homeEntityRepository;
        this.imageService = imageService;
        this.auditService = auditService;
    }


    @Override
    public HomeEntity get() {
        return homeEntityRepository.findAll().get(0);
    }

    @Override
    public boolean save(HomeEntityModel homeEntityModel) {
        if (homeEntityRepository.findAll().size() != 0)
            return false;
        HomeEntity homeEntity = new HomeEntity();
        homeEntity.setName(homeEntityModel.getName());
        homeEntity.setNameEn(homeEntityModel.getNameEn());
        homeEntity.setDescription(homeEntityModel.getDescription());
        homeEntity.setDescriptionEn(homeEntityModel.getDescriptionEn());
        homeEntity.setImages(imageService.saveImages(homeEntityModel.getImages()));
        homeEntityRepository.save(homeEntity);
        return true;
    }

    @Override
    public boolean updateWithoutImages(HomeEntityDto homeEntityDto) {
        HomeEntity oldEntity = homeEntityRepository.findAll().get(0);
        if (!homeEntityDto.getName().equals(oldEntity.getName()))
            oldEntity.setName(homeEntityDto.getName());
        if (!homeEntityDto.getNameEn().equals(oldEntity.getNameEn()))
            oldEntity.setNameEn(homeEntityDto.getNameEn());
        if (!homeEntityDto.getDescription().equals(oldEntity.getDescription()))
            oldEntity.setDescription(homeEntityDto.getDescription());
        if (!homeEntityDto.getDescriptionEn().equals(oldEntity.getDescriptionEn()))
            oldEntity.setDescriptionEn(homeEntityDto.getDescriptionEn());
        homeEntityRepository.save(oldEntity);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(UPDATE_HOME_ENTITY.getAction(), oldEntity.getId()),
                new Date()
        );

        return true;
    }

    @Override
    public boolean update(HomeEntityModel homeEntityModel) {
        HomeEntity oldEntity = homeEntityRepository.findAll().get(0);
        List<Image> oldImages = oldEntity.getImages();
        oldEntity.setImages(imageService.saveImages(homeEntityModel.getImages()));
        imageService.deleteImages(oldImages);
        if (!homeEntityModel.getName().equals(oldEntity.getName()))
            oldEntity.setName(homeEntityModel.getName());
        if (!homeEntityModel.getNameEn().equals(oldEntity.getNameEn()))
            oldEntity.setNameEn(homeEntityModel.getNameEn());
        if (!homeEntityModel.getDescription().equals(oldEntity.getDescription()))
            oldEntity.setDescription(homeEntityModel.getDescription());
        if (!homeEntityModel.getDescriptionEn().equals(oldEntity.getDescriptionEn()))
            oldEntity.setDescriptionEn(homeEntityModel.getDescriptionEn());
        homeEntityRepository.save(oldEntity);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(UPDATE_HOME_ENTITY.getAction(), oldEntity.getId()),
                new Date()
        );

        return true;
    }

    @Override
    public boolean uploadImages(List<MultipartFile> images) {
        HomeEntity oldEntity = homeEntityRepository.findAll().get(0);
        List<Image> oldImages = oldEntity.getImages();
        oldEntity.setImages(imageService.saveImages(images));
        imageService.deleteImages(oldImages);
        homeEntityRepository.save(oldEntity);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(UPDATE_HOME_ENTITY.getAction(), oldEntity.getId()),
                new Date()
        );

        return true;
    }

    @Override
    public boolean delete(HomeEntityDto homeEntityDto) {
        return false;
    }

    @Override
    public void createHomeEntity() throws IOException {
        if (homeEntityRepository.findAll().size() == 0) {
            HomeEntity homeEntity = new HomeEntity();
            homeEntity.setName("Вологда");
            homeEntity.setNameEn("Vologda");
            homeEntity.setDescription("Описание города Вологда для мобильного гида-справочника");
            homeEntity.setDescriptionEn("Description of Vologda for a mobile guide-directory");
            homeEntity.setImages(imageService.saveImages(
                    List.of(
                            convertFileToMultipartFile(
                                    getFileFromURL(
                                            "https://storage.yandexcloud.net/guidevologda/vologda1.jpg",
                                            "",
                                            "vologda1",
                                            ".jpg"
                                    )),
                            convertFileToMultipartFile(
                                    getFileFromURL(
                                            "https://storage.yandexcloud.net/guidevologda/vologda2.jpg",
                                            "",
                                            "vologda2",
                                            ".jpg"
                                    ))
                    )
            ));
            homeEntityRepository.save(homeEntity);

            auditService.saveAction(
                    SYSTEM_ID,
                    SYSTEM_USERNAME,
                    String.format(CREATE_HOME_ENTITY.getAction(), homeEntity.getId()),
                    new Date()
            );
        }
    }
}
