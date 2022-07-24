package ru.neonzoff.guidevologdamvc.dto;

import lombok.Data;
import ru.neonzoff.guidevologdamvc.domain.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author Tseplyaev Dmitry
 */
@Data
public class UserDto {

    private Long id;

    @NotEmpty
    @NotNull
    @Size(min = 4, max = 255)
    private String username;

    @NotEmpty
    @Size(min = 4, max = 255)
    private String password;

    @Email
    @NotEmpty
    @NotNull
    @Size(min = 4, max = 255)
    private String email;

    @NotEmpty
    @NotNull
    @Size(min = 2, max = 255)
    private String firstName;

    @NotEmpty
    @NotNull
    @Size(min = 2, max = 255)
    private String lastName;

    @NotNull
    private boolean active;

    @NotNull
    private Set<Role> roles;
}
