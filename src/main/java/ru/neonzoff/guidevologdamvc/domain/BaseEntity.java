package ru.neonzoff.guidevologdamvc.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "base_entity")
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private TypeEntity typeEntity;

    public void setTypeEntity(TypeEntity typeEntity) {
        this.typeEntity = typeEntity;
        typeEntity.addEntity(this);
    }

    //    название
    @NotNull
    @Size(min = 2, max = 512)
    @Column(name = "name", nullable = false, length = 512)
    private String name;

    //    название на англ
    @NotNull
    @Size(min = 2, max = 512)
    @Column(name = "name_en", nullable = false, length = 512)
    private String nameEn;

    //    описание
    @NotNull
    @Size(min = 10, max = 8192)
    @Column(name = "description", nullable = false, length = 8192)
    private String description;

    //    описание на англ
    @NotNull
    @Size(min = 10, max = 8192)
    @Column(name = "description_en", nullable = false, length = 8192)
    private String descriptionEn;

    //    Связь многие-к-одному к улице
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Street street;

    public void setStreet(Street street) {
        this.street = street;
        street.addEntity(this);
    }

    @Size(max = 255)
    @Column(name = "house_number", length = 255)
    private String houseNumber;

    @Size(max = 1024)
    @Column(name = "pos", length = 1024)
    private String pos;

    //    Связь один-ко-многим на контакты
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entity")
    @ToString.Exclude
    private List<Contact> contacts = new ArrayList<>();

    //    Связь один-ко-многим на изображения
    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_entity")
    @ToString.Exclude
    private List<Image> images = new ArrayList<>();

    //    связь один-ко-многим на пропсы
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entity")
    @ToString.Exclude
    private List<EntityProperties> properties = new ArrayList<>();

    @Size(min = 2, max = 512)
    @Column(name = "work_schedule", nullable = false, length = 512)
    private String workSchedule;

    @Size(min = 2, max = 512)
    @Column(name = "work_schedule_en", nullable = false, length = 512)
    private String workScheduleEn;

    //    Связь многие-ко-многим на теги
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            joinColumns = @JoinColumn(name = "id_entity"),
            inverseJoinColumns = @JoinColumn(name = "id_tag")
    )
    private List<Tag> tags = new ArrayList<>();

    public void setTag(Tag tag) {
        tags.add(tag);
        tag.getEntities().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getEntities().remove(this);
    }

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "id_entity"),
            inverseJoinColumns = @JoinColumn(name = "id_track")
    )
    private List<Track> tracks = new ArrayList<>();

    public void addTrack(Track track) {
        this.tracks.add(track);
        track.getEntities().add(this);
    }

    @Transactional
    public void removeTrack(Track track) {
        tracks.remove(track);
        track.getEntities().remove(this);
    }

    @Column(name = "active")
    private Boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
