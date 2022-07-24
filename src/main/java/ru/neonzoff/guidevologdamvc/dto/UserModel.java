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
public class UserModel {
    private Long id;

    @NotEmpty
    @Size(min = 4, max = 255)
    private String username;

    private String password;

    @Email
    @NotEmpty
    @Size(min = 4, max = 255)
    private String email;

    @NotEmpty
    @Size(min = 2, max = 255)
    private String firstName;

    @NotEmpty
    @Size(min = 2, max = 255)
    private String lastName;

    @NotNull
    private boolean active;

    private Set<String> roles;
}
