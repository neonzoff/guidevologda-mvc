package ru.neonzoff.guidevologdamvc.service;

import org.springframework.web.multipart.MultipartFile;
import ru.neonzoff.guidevologdamvc.domain.HomeEntity;
import ru.neonzoff.guidevologdamvc.dto.HomeEntityDto;
import ru.neonzoff.guidevologdamvc.dto.HomeEntityModel;

import java.io.IOException;
import java.util.List;

/**
 * Класс для работы с репозиторием сущности на главном экране
 *
 * @author Tseplyaev Dmitry
 */
public interface HomeEntityService {
    HomeEntity get();

    boolean save(HomeEntityModel homeEntityModel);

    boolean update(HomeEntityModel homeEntityModel);

    boolean updateWithoutImages(HomeEntityDto homeEntityDto);

    boolean delete(HomeEntityDto homeEntityDto);

    boolean uploadImages(List<MultipartFile> images);

    void createHomeEntity() throws IOException;
}
