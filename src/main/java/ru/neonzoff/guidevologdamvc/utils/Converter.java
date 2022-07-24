package ru.neonzoff.guidevologdamvc.utils;

import com.amazonaws.util.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Contact;
import ru.neonzoff.guidevologdamvc.domain.ContactType;
import ru.neonzoff.guidevologdamvc.domain.EntityProperties;
import ru.neonzoff.guidevologdamvc.domain.HomeEntity;
import ru.neonzoff.guidevologdamvc.domain.Image;
import ru.neonzoff.guidevologdamvc.domain.Role;
import ru.neonzoff.guidevologdamvc.domain.Street;
import ru.neonzoff.guidevologdamvc.domain.Tag;
import ru.neonzoff.guidevologdamvc.domain.Track;
import ru.neonzoff.guidevologdamvc.domain.TypeEntity;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityDto;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityModel;
import ru.neonzoff.guidevologdamvc.dto.ContactDto;
import ru.neonzoff.guidevologdamvc.dto.ContactTypeDto;
import ru.neonzoff.guidevologdamvc.dto.EntityPropertiesDto;
import ru.neonzoff.guidevologdamvc.dto.HomeEntityDto;
import ru.neonzoff.guidevologdamvc.dto.ImageDto;
import ru.neonzoff.guidevologdamvc.dto.StreetDto;
import ru.neonzoff.guidevologdamvc.dto.TagDto;
import ru.neonzoff.guidevologdamvc.dto.TrackDto;
import ru.neonzoff.guidevologdamvc.dto.TypeEntityDto;
import ru.neonzoff.guidevologdamvc.dto.UserDto;
import ru.neonzoff.guidevologdamvc.dto.UserModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Tseplyaev Dmitry
 */
public class Converter {

    private Converter() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static List<BaseEntityModel> convertBaseEntitiesToList(List<BaseEntity> baseEntities) {
        List<BaseEntityModel> list = new ArrayList<>();
        for (BaseEntity entity : baseEntities) {
            list.add(convertBaseEntityToModel(entity));
        }

        return list;
    }

    public static BaseEntityModel convertBaseEntityToModel(BaseEntity entity) {
        BaseEntityModel model = new BaseEntityModel();
        model.setId(entity.getId());
        model.setTypeEntity(convertTypeEntityToDto(entity.getTypeEntity()));
        model.setName(entity.getName());
        model.setNameEn(entity.getNameEn());
        model.setDescription(entity.getDescription());
        model.setDescriptionEn(entity.getDescriptionEn());
        model.setStreet(convertStreetToDto(entity.getStreet()));
        model.setHouseNumber(entity.getHouseNumber());
        if (entity.getContacts() != null)
            model.setContacts(convertContactsToListDto(entity.getContacts()));
        model.setImages(convertImagesToListFile(entity.getImages()));
        if (entity.getProperties() != null)
        model.setProperties(convertPropertiesToListDto(entity.getProperties()));
        model.setWorkSchedule(entity.getWorkSchedule());
        model.setWorkScheduleEn(entity.getWorkScheduleEn());
        if (entity.getTags() != null)
            model.setTags(convertTagsToListDto(entity.getTags()));
        model.setTracks(convertTracksToListDto(entity.getTracks()));
        model.setActive(entity.getActive());

        return model;
    }

    public static File convertImageToFile(Image image) {
        //        todo: implement s3 method
        return new File(image.getName());
    }

    public static List<File> convertImagesToListFile(List<Image> images) {
        return images.stream().map(Converter::convertImageToFile).collect(Collectors.toList());
    }

    public static Long getTypeEntityId(TypeEntityDto typeEntityDto) {
        return typeEntityDto.getId();
    }

    public static List<ContactTypeDto> convertContactTypesToListDto(List<ContactType> contactTypes) {
        return contactTypes.stream().map(Converter::convertContactTypeToDto).collect(Collectors.toList());
    }

    public static ContactTypeDto convertContactTypeToDto(ContactType contactType) {
        ContactTypeDto contactTypeDto = new ContactTypeDto();
        contactTypeDto.setId(contactType.getId());
        contactTypeDto.setName(contactType.getName());
        contactTypeDto.setNameEn(contactType.getNameEn());
        contactTypeDto.setContacts(convertContactsToListDto(contactType.getContacts()));

        return contactTypeDto;
    }

    public static ContactType convertContactTypeDto(ContactTypeDto contactTypeDto) {
        ContactType contactType = new ContactType();
        contactType.setId(contactTypeDto.getId());
        contactType.setName(contactTypeDto.getName());
        contactType.setNameEn(contactTypeDto.getNameEn());
        contactType.setContacts(convertContactsDtoToList(contactTypeDto.getContacts()));

        return contactType;
    }

    public static List<EntityPropertiesDto> convertPropertiesToListDto(List<EntityProperties> properties) {
        return properties.stream().map(Converter::convertPropertiesToDto).collect(Collectors.toList());
    }

    public static EntityPropertiesDto convertPropertiesToDto(EntityProperties properties) {
        EntityPropertiesDto propertiesDto = new EntityPropertiesDto();
        propertiesDto.setId(properties.getId());
        propertiesDto.setKey(properties.getKey());
        propertiesDto.setValue(properties.getValue());

        return propertiesDto;
    }

    public static List<EntityProperties> convertPropertiesDtoToList(List<EntityPropertiesDto> propertiesDto) {
        return propertiesDto.stream().map(Converter::convertPropertiesDto).collect(Collectors.toList());
    }

    public static EntityProperties convertPropertiesDto(EntityPropertiesDto entityPropertiesDto) {
        EntityProperties entityProperties = new EntityProperties();
        entityProperties.setId(entityPropertiesDto.getId());
        entityProperties.setKey(entityPropertiesDto.getKey());
        entityProperties.setValue(entityPropertiesDto.getValue());

        return entityProperties;
    }

    public static List<ContactDto> convertContactsToListDto(List<Contact> contacts) {
        return contacts.stream().map(Converter::convertContactToDto).collect(Collectors.toList());
    }

    public static ContactDto convertContactToDto(Contact contact) {
        ContactDto contactDto = new ContactDto();
        contactDto.setId(contact.getId());
        contactDto.setValue(contact.getValue());

        return contactDto;
    }

    public static List<Contact> convertContactsDtoToList(List<ContactDto> contactsDto) {
        return contactsDto.stream().map(Converter::convertContactDto).collect(Collectors.toList());
    }

    public static Contact convertContactDto(ContactDto contactDto) {
        Contact contact = new Contact();
        contact.setId(contactDto.getId());
        contact.setValue(contactDto.getValue());

        return contact;
    }

    public static List<TypeEntityDto> convertTypeEntitiesToListDto(List<TypeEntity> typeEntities) {
        return typeEntities.stream().map(Converter::convertTypeEntityToDto).collect(Collectors.toList());
    }

    public static TypeEntityDto convertTypeEntityToDto(TypeEntity typeEntity) {
        TypeEntityDto typeEntityDto = new TypeEntityDto();
        typeEntityDto.setId(typeEntity.getId());
        typeEntityDto.setName(typeEntity.getName());
        typeEntityDto.setNameEn(typeEntity.getNameEn());
        typeEntityDto.setDescription(typeEntity.getDescription());
        typeEntityDto.setDescriptionEn(typeEntity.getDescriptionEn());
        typeEntityDto.setImage(typeEntity.getImage().getName());

        return typeEntityDto;
    }

    public static List<TypeEntity> convertTypeEntitiesDtoToList(List<TypeEntityDto> typeEntitiesDto) {
        return typeEntitiesDto.stream().map(Converter::convertTypeEntityDto).collect(Collectors.toList());
    }

    public static TypeEntity convertTypeEntityDto(TypeEntityDto typeEntityDto) {
        TypeEntity typeEntity = new TypeEntity();
        typeEntity.setId(typeEntityDto.getId());
        typeEntity.setName(typeEntityDto.getName());
        typeEntity.setNameEn(typeEntityDto.getNameEn());

        return typeEntity;
    }

    public static List<BaseEntityDto> convertBaseEntitiesToListDto(List<BaseEntity> baseEntities) {
        return baseEntities.stream().map(Converter::convertBaseEntityToDto).collect(Collectors.toList());
    }

    public static BaseEntityDto convertBaseEntityToDto(BaseEntity baseEntity) {
        BaseEntityDto baseEntityDto = new BaseEntityDto();
        baseEntityDto.setId(baseEntity.getId());
        baseEntityDto.setTypeEntity(baseEntity.getTypeEntity().getId());
        baseEntityDto.setName(baseEntity.getName());
        baseEntityDto.setNameEn(baseEntity.getNameEn());
        baseEntityDto.setDescription(baseEntity.getDescription());
        baseEntityDto.setDescriptionEn(baseEntity.getDescriptionEn());
        baseEntityDto.setStreet(baseEntity.getStreet().getId());
        baseEntityDto.setHouseNumber(baseEntity.getHouseNumber());
        baseEntityDto.setPos(baseEntity.getPos());
        baseEntityDto.setContacts(convertContactsToListDto(baseEntity.getContacts()));
        baseEntityDto.setImages(convertImagesToListDto(baseEntity.getImages()));
        baseEntityDto.setProperties(convertPropertiesToListDto(baseEntity.getProperties()));
        baseEntityDto.setWorkSchedule(baseEntity.getWorkSchedule());
        baseEntityDto.setWorkScheduleEn(baseEntity.getWorkScheduleEn());
        baseEntityDto.setTags(convertTagsToListIds(baseEntity.getTags()));
        for (Track track : baseEntity.getTracks())
            baseEntityDto.getTracks().add(track.getId());
        baseEntityDto.setActive(baseEntity.getActive());

        return baseEntityDto;
    }


    public static List<Long> convertTagsToListIds(List<Tag> tags) {
        return tags.stream().map(Tag::getId).collect(Collectors.toList());
    }

    public static List<ImageDto> convertImagesToListDto(List<Image> images) {
        return images.stream().map(Converter::convertImageToDto).collect(Collectors.toList());
    }

    public static ImageDto convertImageToDto(Image image) {
        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setName(image.getName());

        return imageDto;
    }

    public static List<Image> convertImagesDtoToList(List<ImageDto> imagesDto) {
        return imagesDto.stream().map(Converter::convertImageDto).collect(Collectors.toList());
    }

    public static Image convertImageDto(ImageDto imageDto) {
        Image image = new Image();
        image.setId(imageDto.getId());
        image.setName(imageDto.getName());

        return image;
    }

    public static List<StreetDto> convertStreetsToListDto(List<Street> streets) {
        return streets.stream().map(Converter::convertStreetToDto).collect(Collectors.toList());
    }

    public static StreetDto convertStreetToDto(Street street) {
        StreetDto streetDto = new StreetDto();
        streetDto.setId(street.getId());
        streetDto.setName(street.getName());
        streetDto.setNameEn(street.getNameEn());

        return streetDto;
    }

    public static List<Street> convertStreetsDtoToList(List<StreetDto> streetsDto) {
        return streetsDto.stream().map(Converter::convertStreetDto).collect(Collectors.toList());
    }

    public static Street convertStreetDto(StreetDto streetDto) {
        Street street = new Street();
        street.setId(streetDto.getId());
        street.setName(streetDto.getName());
        street.setNameEn(streetDto.getNameEn());

        return street;
    }

    public static List<TagDto> convertTagsToListDto(List<Tag> tags) {
        return tags.stream().map(Converter::convertTagToDto).collect(Collectors.toList());
    }

    public static TagDto convertTagToDto(Tag tag) {
        TagDto tagDto = new TagDto();
        tagDto.setId(tag.getId());
        tagDto.setName(tag.getName());
        tagDto.setNameEn(tag.getNameEn());

        return tagDto;
    }

    public static List<Tag> convertTagsDtoToList(List<TagDto> tagsDto) {
        return tagsDto.stream().map(Converter::convertTagDto).collect(Collectors.toList());
    }

    public static Tag convertTagDto(TagDto tagDto) {
        Tag tag = new Tag();
        tag.setId(tagDto.getId());
        tag.setName(tagDto.getName());
        tag.setNameEn(tagDto.getNameEn());

        return tag;
    }

    public static UserDto convertUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setActive(user.isActive());
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());

        return userDto;
    }

    public static List<UserDto> convertUsersToListDto(List<User> users) {
        return users.stream().map(Converter::convertUserToDto).collect(Collectors.toList());
    }

    public static Set<String> convertSetRolesToStrings(Set<Role> roles) {
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }

    public static UserModel convertUserToModel(User user) {
        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setUsername(user.getUsername());
        userModel.setEmail(user.getEmail());
        userModel.setFirstName(user.getFirstName());
        userModel.setLastName(user.getLastName());
        userModel.setRoles(convertSetRolesToStrings(user.getRoles()));
        userModel.setActive(user.isActive());

        return userModel;
    }

    public static List<TrackDto> convertTracksToListDto(List<Track> tracks) {
        return tracks.stream().map(Converter::convertTrackToDto).collect(Collectors.toList());
    }

    public static TrackDto convertTrackToDto(Track track) {
        TrackDto trackDto = new TrackDto();
        trackDto.setId(track.getId());
        trackDto.setName(track.getName());
        trackDto.setNameEn(track.getNameEn());
        trackDto.setDescription(track.getDescription());
        trackDto.setDescriptionEn(track.getDescriptionEn());
        trackDto.setImage(convertImageToDto(track.getImage()));
        for (BaseEntity entity : track.getEntities())
            trackDto.getEntities().add(convertBaseEntityToDto(entity));

        return trackDto;
    }

    public static HomeEntityDto convertHomeEntityToDto(HomeEntity homeEntity) {
        HomeEntityDto homeEntityDto = new HomeEntityDto();
        homeEntityDto.setId(homeEntity.getId());
        homeEntityDto.setName(homeEntity.getName());
        homeEntityDto.setNameEn(homeEntity.getNameEn());
        homeEntityDto.setDescription(homeEntity.getDescription());
        homeEntityDto.setDescriptionEn(homeEntity.getDescriptionEn());
        homeEntityDto.setImages(convertImagesToListDto(homeEntity.getImages()));

        return homeEntityDto;
    }

    public static File getFileFromURL(String url, String pathToSave, String filename, String formatName) throws IOException {
        URL outUrl = new URL(url);
        File output = new File(pathToSave + filename + formatName);
        ReadableByteChannel rbc = Channels.newChannel(outUrl.openStream());
        FileOutputStream fos = new FileOutputStream(output);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        return output;
    }

    public static File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convertedFile;
    }

    public static MultipartFile convertFileToMultipartFile(final File file) throws IOException {
        MultipartFile multipartFile;

        try (final FileInputStream input = new FileInputStream(file)) {
            multipartFile = new MockMultipartFile(
                    file.getName(),
                    file.getName(),
                    Files.probeContentType(file.toPath()),
                    IOUtils.toByteArray(input));
        }

        return multipartFile;
    }

}
