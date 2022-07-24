package ru.neonzoff.guidevologdamvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackDto {
    private Long id;

    @NotNull
    @Size(min = 2, max = 512)
    private String name;

    @NotNull
    @Size(min = 2, max = 512)
    private String nameEn;

    @NotNull
    @Size(min = 10, max = 8192)
    private String description;

    @NotNull
    @Size(min = 10, max = 8192)
    private String descriptionEn;

    @NotNull
    private ImageDto image;

    private List<BaseEntityDto> entities = new ArrayList<>();
}
