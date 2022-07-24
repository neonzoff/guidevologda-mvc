package ru.neonzoff.guidevologdamvc.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Tseplyaev Dmitry
 */
@Data
public class TypeEntityModel {
    private Long id;

    @NotNull
    @Size(min = 2, max = 512)
    private String name;

    @NotNull
    @Size(min = 2, max = 512)
    private String nameEn;

    @NotNull
    @Size(min = 10, max = 2048)
    private String description;

    @NotNull
    @Size(min = 10, max = 2048)
    private String descriptionEn;

    @NotNull
    private MultipartFile image;
}
