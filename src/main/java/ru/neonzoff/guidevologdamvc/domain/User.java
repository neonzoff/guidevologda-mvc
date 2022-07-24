package ru.neonzoff.guidevologdamvc.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

/**
 * @author Tseplyaev Dmitry
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 4, max = 255)
    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @NotNull
    @Size(min = 4, max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Transient
    transient private String passwordConfirm;

    @Email
    @NotNull
    @Size(min = 4, max = 255)
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @NotNull
    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role"))
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}
