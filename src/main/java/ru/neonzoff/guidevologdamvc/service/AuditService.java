package ru.neonzoff.guidevologdamvc.service;

import ru.neonzoff.guidevologdamvc.domain.AuditAction;

import java.util.Date;
import java.util.List;

/**
 * Класс для работы с репозиторием журналирования
 *
 * @author Tseplyaev Dmitry
 */
public interface AuditService {

    /**
     * @param userId    идентификатор пользователя
     * @param username  логин пользователя
     * @param action    фиксируемое действие
     * @param timestamp фиксируемое время
     */
    void saveAction(Long userId, String username, String action, Date timestamp);

    /**
     * @return список зафиксированных действий
     */
    List<AuditAction> getActions();
}

