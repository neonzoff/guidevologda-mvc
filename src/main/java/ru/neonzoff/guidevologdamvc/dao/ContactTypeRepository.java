package ru.neonzoff.guidevologdamvc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neonzoff.guidevologdamvc.domain.Contact;
import ru.neonzoff.guidevologdamvc.domain.ContactType;

import java.util.Optional;

/**
 * @author Tseplyaev Dmitry
 */
@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Long> {

    Optional<ContactType> findByName(String name);

    ContactType findByContacts(Contact contact);
}
