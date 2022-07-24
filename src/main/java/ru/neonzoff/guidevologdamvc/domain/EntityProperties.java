package ru.neonzoff.guidevologdamvc.domain;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * @author Tseplyaev Dmitry
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "entity_properties")
public class EntityProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 512)
    @Column(name = "key", nullable = false, length = 512)
    private String key;

    @NotNull
    @Size(min = 1, max = 8192)
    @Column(name = "value", nullable = false, length = 8192)
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EntityProperties that = (EntityProperties) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
