package ru.neonzoff.guidevologdamvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neonzoff.guidevologdamvc.dao.BaseEntityRepository;
import ru.neonzoff.guidevologdamvc.dao.ContactRepository;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Contact;
import ru.neonzoff.guidevologdamvc.domain.ContactType;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.ContactDto;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CREATE_CONTACT;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.DELETE_CONTACT;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertContactsToListDto;

/**
 * @author Tseplyaev Dmitry
 */
@Service
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;

    private final BaseEntityRepository baseEntityRepository;

    private final ContactTypeService contactTypeService;

    private final AuditService auditService;

    public ContactServiceImpl(ContactRepository contactRepository, BaseEntityRepository baseEntityRepository,
                              ContactTypeService contactTypeService, AuditService auditService) {
        this.contactRepository = contactRepository;
        this.baseEntityRepository = baseEntityRepository;
        this.contactTypeService = contactTypeService;
        this.auditService = auditService;
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    @Override
    public List<Contact> findAll(String sortColumn) {
        return contactRepository.findAll(Sort.by(sortColumn).ascending());
    }

    @Override
    public Contact findById(Long id) {
//        ifPresent
        return contactRepository.findById(id).get();
    }

    @Override
    public List<Contact> findByValue(String value) {
//        ifPresent
        return contactRepository.findByValue(value);
    }

    @Override
    public List<Contact> findByBaseEntity(BaseEntity baseEntity) {
//        ifPresent
        return baseEntityRepository.findByName(baseEntity.getName()).get().getContacts();
    }

    @Override
    public List<Contact> findByType(ContactType contactType) {
        return contactTypeService.findByName(contactType.getName()).getContacts();
    }

    @Transactional
    @Override
    public Long saveContact(ContactDto contactDto) {
        Contact contact = new Contact();
        contact.setValue(contactDto.getValue().trim());
        contactRepository.save(contact);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(CREATE_CONTACT.getAction(), contact.getId()),
                new Date()
        );
        return contact.getId();
    }

    @Transactional
    @Override
    public boolean updateContact(ContactDto contactDto) {
        return false;
    }

    @Transactional
    @Override
    public boolean deleteContacts(List<Contact> contacts) {
        contacts.forEach(this::deleteContact);
        return true;
    }

    @Transactional
    @Override
    public boolean deleteContact(Contact contact) {
        contactRepository.delete(contact);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(DELETE_CONTACT.getAction(), contact.getId()),
                new Date()
        );
        return true;
    }

    @Override
    public Page<ContactDto> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Contact> contacts = contactRepository.findAll(pageable.getSort());
        List<ContactDto> contactsDto;

        if (contacts.size() < startItem) {
            contactsDto = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, contacts.size());
            contactsDto = convertContactsToListDto(contacts.subList(startItem, toIndex));
        }

        return new PageImpl<>(contactsDto, PageRequest.of(currentPage, pageSize), contacts.size());
    }
}
