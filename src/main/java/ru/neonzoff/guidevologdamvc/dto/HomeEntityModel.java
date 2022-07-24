package ru.neonzoff.guidevologdamvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeEntityModel {
    private Long id;

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
    private List<MultipartFile> images = new ArrayList<>();
}
