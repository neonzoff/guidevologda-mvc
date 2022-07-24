package ru.neonzoff.guidevologdamvc.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Tseplyaev Dmitry
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "streets")
public class Street {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name", nullable = false, length = 512)
    private String name;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name_en", nullable = false, length = 512)
    private String nameEn;

    @OneToMany(mappedBy = "street")
    private List<BaseEntity> entities = new ArrayList<>();

    public void addEntity(BaseEntity entity) {
        this.entities.add(entity);
    }

    public void removeEntity(BaseEntity entity) {
        this.entities.remove(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Street street = (Street) o;
        return id != null && Objects.equals(id, street.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
