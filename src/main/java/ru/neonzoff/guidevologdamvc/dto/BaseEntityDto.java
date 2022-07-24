package ru.neonzoff.guidevologdamvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tseplyaev Dmitry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntityDto {

    private Long id;

    private Long typeEntity;

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

    private Long street;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String houseNumber;

    @Size(max = 1024)
    private String pos;

    private List<ImageDto> images = new ArrayList<>();

    private List<ContactDto> contacts = new ArrayList<>();

    private List<EntityPropertiesDto> properties = new ArrayList<>();

    @Size(min = 2, max = 512)
    private String workSchedule;

    @Size(min = 2, max = 512)
    private String workScheduleEn;

    private List<Long> tags = new ArrayList<>();

    private List<Long> tracks = new ArrayList<>();

    private Boolean active;
}
