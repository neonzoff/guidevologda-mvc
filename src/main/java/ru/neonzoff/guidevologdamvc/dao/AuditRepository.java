package ru.neonzoff.guidevologdamvc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neonzoff.guidevologdamvc.domain.AuditAction;

@Repository
public interface AuditRepository extends JpaRepository<AuditAction, Long> {
}




