package ru.neonzoff.guidevologdamvc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neonzoff.guidevologdamvc.dao.RoleRepository;
import ru.neonzoff.guidevologdamvc.dao.UserRepository;
import ru.neonzoff.guidevologdamvc.domain.Role;
import ru.neonzoff.guidevologdamvc.domain.User;
import ru.neonzoff.guidevologdamvc.dto.UserDto;
import ru.neonzoff.guidevologdamvc.dto.UserModel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CHANGE_PROFILE;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.CHANGE_USER_PROFILE;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.INIT_ADMIN_ACCOUNT;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.INIT_ROLES;
import static ru.neonzoff.guidevologdamvc.domain.ACTIONS.REGISTER;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_ID;
import static ru.neonzoff.guidevologdamvc.utils.Constants.SYSTEM_USERNAME;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertUsersToListDto;

/**
 * Класс для работы с пользователями Spring Security
 *
 * @author Tseplyaev Dmitry
 */
@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuditService auditService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    @Lazy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public List<UserDto> allUsersDto() {
        return convertUsersToListDto(allUsers());
    }

    @Transactional
    public boolean editProfile(UserModel userModel) {
        User user = userRepository.getById(userModel.getId());
        boolean isChangeLogin = false;
        boolean isChanged = false;
//        проверяем что логин изменился
        if (!userModel.getUsername().isEmpty() && !userModel.getUsername().equals(user.getUsername())) {
//            проверяем что логин не занят
            if (!userRepository.findByUsername(userModel.getUsername()).isPresent()) {
                isChangeLogin = true;
                user.setUsername(userModel.getUsername().trim());
                isChanged = true;
            }
        }
//        проверяем что пароль не пустой
        if (!userModel.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(userModel.getPassword().trim()));
            isChanged = true;
        }

        if (!userModel.getEmail().isEmpty() && !userModel.getEmail().equals(user.getEmail())) {
            user.setEmail(userModel.getEmail().trim());
            isChanged = true;
        }

        if (!userModel.getFirstName().isEmpty() && !userModel.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userModel.getFirstName().trim());
            isChanged = true;
        }

        if (!userModel.getLastName().isEmpty() && !userModel.getLastName().equals(user.getLastName())) {
            user.setLastName(userModel.getLastName().trim());
            isChanged = true;
        }

        userRepository.save(user);
        if (isChanged)
            auditService.saveAction(user.getId(), user.getUsername(), CHANGE_PROFILE.getAction(), new Date());
        return isChangeLogin;
    }

    @Transactional
    public boolean editUser(UserModel userModel) {
        User user = userRepository.getById(userModel.getId());
//        проверяем что логин изменился
        if (!userModel.getUsername().isEmpty() && !userModel.getUsername().equals(user.getUsername())) {
//            проверяем что логин не занят
            if (!userRepository.findByUsername(userModel.getUsername()).isPresent()) {
                user.setUsername(userModel.getUsername().trim());
            }
        }
//        проверяем что пароль не пустой
        if (!userModel.getPassword().isEmpty())
            user.setPassword(bCryptPasswordEncoder.encode(userModel.getPassword().trim()));
        if (!userModel.getEmail().isEmpty() && !userModel.getEmail().equals(user.getEmail()))
            user.setEmail(userModel.getEmail().trim());
        if (!userModel.getFirstName().isEmpty() && !userModel.getFirstName().equals(user.getFirstName()))
            user.setFirstName(userModel.getFirstName().trim());
        if (!userModel.getLastName().isEmpty() && !userModel.getLastName().equals(user.getLastName()))
            user.setLastName(userModel.getLastName().trim());
        if (user.isActive() != userModel.isActive())
            user.setActive(userModel.isActive());
        if (!user.getRoles().equals(userModel.getRoles())) {
            Set<Role> roles = new HashSet<>();
            for (String role : userModel.getRoles()) {
                roles.add(roleRepository.findByName(role).get());
            }
//            защита от дураков :)
            roles.add(roleRepository.findByName("ROLE_USER").get());
            if (userModel.getId() == 1) {
                roles.addAll(roleRepository.findAll());
            }
            user.setRoles(roles);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        auditService.saveAction(
                currentUser.getId(),
                currentUser.getUsername(),
                String.format(CHANGE_USER_PROFILE.getAction(), user.getId(), user.getUsername()),
                new Date()
        );

        return true;
    }

    @Transactional
    public boolean saveUser(User user) {
        Optional<User> userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB.isPresent()) {
            return false;
        } else {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_USER").get());
            user.setRoles(roles);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            user.setActive(false);
            userRepository.save(user);
            auditService.saveAction(user.getId(), user.getUsername(), String.format(REGISTER.getAction(), user.getId()), new Date());
            return true;
        }
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }

    @Transactional
    public boolean createRoles() {
        if (roleRepository.count() == 0) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
            Role editorRole = new Role();
            editorRole.setName("ROLE_EDITOR");
            roleRepository.save(editorRole);
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            auditService.saveAction(
                    SYSTEM_ID,
                    SYSTEM_USERNAME,
                    String.format(INIT_ROLES.getAction(), userRole.getName() + "," + editorRole.getName() + "," + adminRole.getName()),
                    new Date()
            );
            return true;
        }
        return false;
    }

    @Transactional
    public boolean createAdminAccount() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setEmail("admin@admin.com");
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_ADMIN").get());
            roles.add(roleRepository.findByName("ROLE_EDITOR").get());
            roles.add(roleRepository.findByName("ROLE_USER").get());
            admin.setRoles(roles);
            admin.setActive(true);
            admin.setFirstName("Администратор");
            admin.setLastName("Сервиса");
            admin.setUsername(adminUsername);
            admin.setPassword(bCryptPasswordEncoder.encode(adminPassword));
            userRepository.save(admin);
            auditService.saveAction(
                    SYSTEM_ID,
                    SYSTEM_USERNAME,
                    String.format(INIT_ADMIN_ACCOUNT.getAction(), admin.getUsername()),
                    new Date());
            return true;
        }
        return false;
    }

    public Page<UserDto> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<User> users = userRepository.findAll(pageable.getSort());
        List<UserDto> usersDto;

        if (users.size() < startItem) {
            usersDto = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, users.size());
            usersDto = convertUsersToListDto(users.subList(startItem, toIndex));
        }

        Page<UserDto> userPage = new PageImpl<>(usersDto, PageRequest.of(currentPage, pageSize), users.size());

        return userPage;
    }

    @Transactional
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
