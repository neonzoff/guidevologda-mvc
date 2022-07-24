package ru.neonzoff.guidevologdamvc.service;

import org.springframework.stereotype.Service;
import ru.neonzoff.guidevologdamvc.dao.AuditRepository;
import ru.neonzoff.guidevologdamvc.domain.AuditAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void saveAction(Long userId, String username, String action, Date timestamp) {
        AuditAction auditAction = new AuditAction(userId, username, action, timestamp);
        auditRepository.save(auditAction);
    }

    @Override
    public List<AuditAction> getActions() {
        return new ArrayList<>(auditRepository.findAll());
    }
}
