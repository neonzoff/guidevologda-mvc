package ru.neonzoff.guidevologdamvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackModel {
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
    private MultipartFile image;

    @NotNull
    private String entities;
}
