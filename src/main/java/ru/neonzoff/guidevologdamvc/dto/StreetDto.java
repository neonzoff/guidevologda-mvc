package ru.neonzoff.guidevologdamvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Tseplyaev Dmitry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreetDto {

    private Long id;

    @NotEmpty
    @NotNull
    @Size(min = 2, max = 512)
    private String name;

    @NotEmpty
    @NotNull
    @Size(min = 2, max = 512)
    private String nameEn;
}
