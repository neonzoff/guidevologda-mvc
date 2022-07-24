package ru.neonzoff.guidevologdamvc.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tseplyaev Dmitry
 */
@Data
public class ContactTypeDto {
    private Long id;

    @NotNull
    @Size(min = 2, max = 255)
    private String name;

    @NotNull
    @Size(min = 2, max = 255)
    private String nameEn;

    private List<ContactDto> contacts = new ArrayList<>();
}
