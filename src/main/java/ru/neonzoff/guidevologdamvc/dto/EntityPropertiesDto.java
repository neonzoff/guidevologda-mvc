package ru.neonzoff.guidevologdamvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Tseplyaev Dmitry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityPropertiesDto {
    private Long id;

    @NotNull
    @Size(min = 2, max = 512)
    private String key;

    @NotNull
    @Size(min = 1, max = 8192)
    private String value;
}
