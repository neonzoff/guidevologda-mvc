package ru.neonzoff.guidevologdamvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.util.List;

/**
 * @author Tseplyaev Dmitry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntityModel {

    private Long id;

    @NotNull
    private TypeEntityDto typeEntity;

    @NotNull
    @NotEmpty
    @Size(min = 2, max = 512)
    private String name;

    @NotNull
    @NotEmpty
    @Size(min = 2, max = 512)
    private String nameEn;

    @NotNull
    @NotEmpty
    @Size(min = 10, max = 8192, message = "Размер описания должен быть от 10 до 8192 символов")
    private String description;

    @NotNull
    @NotEmpty
    @Size(min = 10, max = 8192, message = "Размер описания на английском должен быть от 10 до 8192 символов")
    private String descriptionEn;

    @NotNull
    private StreetDto street;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String houseNumber;

    private List<ContactDto> contacts;

    @NotNull
    private List<File> images;

    private List<EntityPropertiesDto> properties;

    @Size(min = 2, max = 512)
    private String workSchedule;

    @Size(min = 2, max = 512)
    private String workScheduleEn;

    private List<TagDto> tags;

    private List<TrackDto> tracks;

    private Boolean active;
}
