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
import ru.neonzoff.guidevologdamvc.dao.StreetRepository;
import ru.neonzoff.guidevologdamvc.domain.Street;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.StreetDto;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CREATE_STREET;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.DELETE_STREET;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.UPDATE_STREET;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertStreetToDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertStreetsToListDto;

/**
 * @author Tseplyaev Dmitry
 */
@Service
public class StreetServiceImpl implements StreetService {
    private final StreetRepository streetRepository;

    private final AuditService auditService;

    public StreetServiceImpl(StreetRepository streetRepository, AuditService auditService) {
        this.streetRepository = streetRepository;
        this.auditService = auditService;
    }

    @Override
    public List<StreetDto> findAll() {
        return convertStreetsToListDto(streetRepository.findAll());
    }

    @Override
    public List<StreetDto> findAll(String sortColumn) {
        return convertStreetsToListDto(streetRepository.findAll(Sort.by(sortColumn).ascending()));
    }

    @Override
    public StreetDto findByName(String name) {
//                обработать если не найден
        return convertStreetToDto(streetRepository.findByName(name).get());
    }

    @Transactional
    @Override
    public boolean saveStreet(StreetDto streetDto) {
        Street newStreet = new Street();
        newStreet.setName(streetDto.getName());
        newStreet.setNameEn(streetDto.getNameEn());
        if (streetRepository.findByName(streetDto.getName()).isPresent()) {
            return false;
        }
        streetRepository.save(newStreet);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(CREATE_STREET.getAction(), newStreet.getId()),
                new Date()
        );

        return true;
    }

    @Transactional
    @Override
    public boolean updateStreet(StreetDto streetDto) {
        Street oldStreet = streetRepository.getById(streetDto.getId());
        if (oldStreet.getName().equals(streetDto.getName())) {
            return false;
        }
//        todo: а сохранился ли он измененный? Проверить.
        oldStreet.setName(streetDto.getName());
        oldStreet.setNameEn(streetDto.getNameEn());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(UPDATE_STREET.getAction(), oldStreet.getId()),
                new Date()
        );
        return true;
    }

    @Transactional
    @Override
    public boolean deleteStreet(StreetDto streetDto) {
        Optional<Street> oldStreet = streetRepository.findById(streetDto.getId());
        if (oldStreet.isPresent() && oldStreet.get().getEntities().isEmpty()) {
            streetRepository.delete(oldStreet.get());

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            auditService.saveAction(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    String.format(DELETE_STREET.getAction(), oldStreet.get().getId()),
                    new Date()
            );
            return true;
        }
        return false;
    }

    @Override
    public Street findById(Long id) {
//        ifPresent
        return streetRepository.findById(id).get();
    }

    @Override
    public Page<StreetDto> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Street> streets = streetRepository.findAll(pageable.getSort());
        List<StreetDto> streetsDto;

        if (streets.size() < startItem) {
            streetsDto = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, streets.size());
            streetsDto = convertStreetsToListDto(streets.subList(startItem, toIndex));
        }

        return new PageImpl<>(streetsDto, PageRequest.of(currentPage, pageSize), streets.size());
    }
}
