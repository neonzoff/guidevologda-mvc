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
import javax.persistence.OneToOne;
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
@Table(name = "type_entity")
public class TypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 512)
    @Column(name = "name", nullable = false, length = 512)
    private String name;

    @NotNull
    @Size(min = 2, max = 512)
    @Column(name = "name_en", nullable = true, length = 512)
    private String nameEn;

    @NotNull
    @Size(min = 10, max = 2048)
    @Column(name = "description", nullable = false, length = 2048)
    private String description;

    @NotNull
    @Size(min = 10, max = 2048)
    @Column(name = "description_en", nullable = false, length = 2048)
    private String descriptionEn;

    @OneToOne
    private Image image;

    @OneToMany(mappedBy = "typeEntity")
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
        TypeEntity that = (TypeEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
