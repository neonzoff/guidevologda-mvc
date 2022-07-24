package ru.neonzoff.guidevologdamvc.domain;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

/**
 * @author Tseplyaev Dmitry
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "contact_type")
public class ContactType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(min = 2, max = 255)
    @Column(name = "name_en", nullable = false, length = 255)
    private String nameEn;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type")
    @ToString.Exclude
    private List<Contact> contacts = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ContactType that = (ContactType) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
