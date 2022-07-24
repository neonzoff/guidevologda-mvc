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
import ru.neonzoff.guidevologdamvc.dao.ContactTypeRepository;
import ru.neonzoff.guidevologdamvc.domain.Contact;
import ru.neonzoff.guidevologdamvc.domain.ContactType;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.ContactTypeDto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CREATE_CONTACT_TYPE;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.DELETE_CONTACT_TYPE;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.INIT_CONTACT_TYPE;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.UPDATE_CONTACT_TYPE;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_ID;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_USERNAME;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertContactTypesToListDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertContactsDtoToList;

/**
 * @author Tseplyaev Dmitry
 */
@Service
public class ContactTypeServiceImpl implements ContactTypeService {
    private final ContactTypeRepository repository;

    private final AuditService auditService;

    public ContactTypeServiceImpl(ContactTypeRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Override
    public List<ContactType> findAll() {
        return repository.findAll();
    }

    @Override
    public ContactType findById(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public List<ContactType> findAll(String sortColumn) {
        return repository.findAll(Sort.by(sortColumn).ascending());
    }

    @Override
    public ContactType findByName(String name) {
//        обработать если не найден
        return repository.findByName(name).get();
    }

    @Override
    public ContactType findByContacts(Contact contact) {
        return repository.findByContacts(contact);
    }

    @Override
    @Transactional
    public boolean addContact(String name, Contact contact) {
//        ifPresent
        repository.findByName(name).get().getContacts().add(contact);
        return true;
    }

    @Override
    public boolean removeContact(ContactType contactType, Contact contact) {
        repository.findById(contactType.getId()).get().getContacts().remove(contact);
        return true;
    }

    @Override
    public List<Contact> getAllContacts(String name) {
        return findByName(name).getContacts();
    }

    @Override
    @Transactional
    public boolean saveContactType(ContactTypeDto contactTypeDto) {
        ContactType contactType = new ContactType();
        contactType.setName(contactTypeDto.getName());
        contactType.setNameEn(contactTypeDto.getNameEn());
//        nullable
        if (contactTypeDto.getContacts() != null)
            contactType.setContacts(convertContactsDtoToList(contactTypeDto.getContacts()));
        if (repository.findByName(contactTypeDto.getName()).isPresent())
            return false;
        repository.save(contactType);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(CREATE_CONTACT_TYPE.getAction(), contactType.getId()),
                new Date()
        );
        return true;
    }

    @Override
    @Transactional
    public boolean updateContactType(ContactTypeDto contactTypeDto) {
        ContactType contactType = repository.getById(contactTypeDto.getId());
        if (contactType.getName().equals(contactTypeDto.getName()) && contactType.getNameEn().equals(contactTypeDto.getNameEn()))
            return false;
//                todo: а сохранился ли он измененный? Проверить.
//        для обоих меняем
        contactType.setName(contactTypeDto.getName());
        contactType.setNameEn(contactTypeDto.getNameEn());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(UPDATE_CONTACT_TYPE.getAction(), contactType.getId()),
                new Date()
        );
        return true;
    }

    @Override
    @Transactional
    public boolean deleteContactType(ContactTypeDto contactTypeDto) {
        Optional<ContactType> contactType = repository.findById(contactTypeDto.getId());
        if (contactType.isPresent() && contactType.get().getContacts().isEmpty()) {
            repository.delete(contactType.get());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            auditService.saveAction(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    String.format(DELETE_CONTACT_TYPE.getAction(), contactType.get().getId()),
                    new Date()
            );
            return true;
        }
        return false;
    }

    @Override
    public Page<ContactTypeDto> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<ContactType> contactTypes = repository.findAll(pageable.getSort());
        List<ContactTypeDto> contactTypesDto;

        if (contactTypes.size() < startItem) {
            contactTypesDto = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, contactTypes.size());
            contactTypesDto = convertContactTypesToListDto(contactTypes.subList(startItem, toIndex));
        }

        return new PageImpl<>(contactTypesDto, PageRequest.of(currentPage, pageSize), contactTypes.size());
    }

    @Override
    @Transactional
    public void createDefaultTypes() {
        if (repository.findAll().isEmpty()) {
            ContactType typePhone = new ContactType();
            typePhone.setName("Номер телефона");
            typePhone.setNameEn("Phone number");
            repository.save(typePhone);

            ContactType typeEmail = new ContactType();
            typeEmail.setName("Email");
            typeEmail.setNameEn("Email");
            repository.save(typeEmail);

            ContactType typeVk = new ContactType();
            typeVk.setName("ВК");
            typeVk.setNameEn("VK");
            repository.save(typeVk);

            auditService.saveAction(
                    SYSTEM_ID,
                    SYSTEM_USERNAME,
                    String.format(INIT_CONTACT_TYPE.getAction(), typePhone.getId() + "," + typeEmail.getId() + "," + typeVk.getId()),
                    new Date()
            );
        }
    }
}
