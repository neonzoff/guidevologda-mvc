package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Contact;
import ru.neonzoff.guidevologdamvc.domain.ContactType;
import ru.neonzoff.guidevologdamvc.dto.ContactDto;

import java.util.List;

/**
 * Класс для работы с репозиторием контактов
 *
 * @author Tseplyaev Dmitry
 */
public interface ContactService {
    List<Contact> findAll();

    List<Contact> findAll(String sortColumn);

    Contact findById(Long id);

    List<Contact> findByValue(String value);

    List<Contact> findByBaseEntity(BaseEntity baseEntity);

    List<Contact> findByType(ContactType contactType);

    Long saveContact(ContactDto contactDto);

    boolean updateContact(ContactDto contactDto);

    boolean deleteContact(Contact contact);

    boolean deleteContacts(List<Contact> contacts);

    Page<ContactDto> findPaginated(Pageable pageable);
}
