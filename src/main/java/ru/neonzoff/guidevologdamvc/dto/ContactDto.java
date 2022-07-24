package ru.neonzoff.guidevologdamvc.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Tseplyaev Dmitry
 */
@Data
public class ContactDto {
    private Long id;

    @NotNull
    @Size(min = 2, max = 512)
    private String value;
}
