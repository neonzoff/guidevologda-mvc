package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.neonzoff.guidevologdamvc.dao.ImageRepository;
import ru.neonzoff.guidevologdamvc.domain.Image;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.ImageDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.DELETE_IMAGE;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.UPLOAD_IMAGE;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_ID;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_USERNAME;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertImagesToListDto;

/**
 * @author Tseplyaev Dmitry
 */
@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository repository;

    private final StorageService storageService;

    private final AuditService auditService;

    public ImageServiceImpl(ImageRepository repository, StorageService storageService,
                            AuditService auditService) {
        this.repository = repository;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    @Override
    public List<ImageDto> findAll() {
        return convertImagesToListDto(repository.findAll());
    }

    @Override
    public List<ImageDto> findAll(String sortColumn) {
        return null;
    }

    @Override
    public ImageDto findByName(String name) {
        return null;
    }

    @Override
    @Transactional
    public Image saveImage(MultipartFile file) {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        saveImg(file);
        if (!repository.findByName(image.getName()).isPresent()) {
            repository.save(image);

            auditService.saveAction(
                    SYSTEM_ID,
                    SYSTEM_USERNAME,
                    String.format(UPLOAD_IMAGE.getAction(), image.getId()),
                    new Date()
            );

            return image;
        }
        return repository.findByName(image.getName()).get();
    }

    @Override
    @Transactional
    public List<Image> saveImages(List<MultipartFile> files) {
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            images.add(saveImage(file));
        }
        return images;
    }

    @Override
    @Transactional
    public boolean saveImg(MultipartFile file) {
        storageService.uploadFile(file);
        return true;
    }

    @Override
    public boolean saveImgs(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            saveImage(file);
        }
        return true;
    }

    @Override
    public boolean deleteImage(Image image) {
        if (repository.findByName(image.getName()).isPresent()) {
            repository.delete(image);
            storageService.deleteFile(image.getName());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            auditService.saveAction(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    String.format(DELETE_IMAGE.getAction(), image.getId()),
                    new Date()
            );

            return true;
        }
        return false;
    }

    @Override
    public boolean deleteImages(List<Image> images) {
        for (Image image : images) {
            deleteImage(image);
        }
        return true;
    }

    @Override
    public Page<ImageDto> findPaginated(Pageable pageable) {
        return null;
    }
}
