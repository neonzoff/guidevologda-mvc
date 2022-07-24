package ru.neonzoff.guidevologdamvc.dto;

import lombok.Data;
import ru.neonzoff.guidevologdamvc.domain.Image;

import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Tseplyaev Dmitry
 */
@Data
public class TypeEntityDto {
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
    private String image;
}
