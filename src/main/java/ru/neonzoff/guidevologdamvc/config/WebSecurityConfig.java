package ru.neonzoff.guidevologdamvc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.neonzoff.guidevologdamvc.service.ContactTypeService;
import ru.neonzoff.guidevologdamvc.service.HomeEntityService;
import ru.neonzoff.guidevologdamvc.service.TagService;
import ru.neonzoff.guidevologdamvc.service.TypeEntityService;
import ru.neonzoff.guidevologdamvc.service.UserService;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * @author Tseplyaev Dmitry
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    private final TypeEntityService typeEntityService;

    private final ContactTypeService contactTypeService;

    private final TagService tagService;

    private final HomeEntityService homeEntityService;

    public WebSecurityConfig(UserService userService, TypeEntityService typeEntityService,
                             ContactTypeService contactTypeService, TagService tagService,
                             HomeEntityService homeEntityService
    ) {
        this.userService = userService;
        this.typeEntityService = typeEntityService;
        this.contactTypeService = contactTypeService;
        this.tagService = tagService;
        this.homeEntityService = homeEntityService;
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        backup
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                //Доступ только для не зарегистрированных пользователей
                .antMatchers("/registration").not().fullyAuthenticated()
                //Доступ только для пользователей с ролью Администратор
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/edit/**").hasAnyRole("EDITOR", "ADMIN")
                //Доступ разрешен всем пользователей
                .antMatchers("/resources/**", "/css/**", "/rest/**").permitAll()
                //Все остальные страницы требуют аутентификации
                .anyRequest().authenticated()
                .and()
                //Настройка для входа в систему
                .formLogin()
                .loginPage("/login")
                //Перенарпавление на главную страницу после успешного входа
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .permitAll();
//                .logoutSuccessUrl("/");
    }


    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }

    @PostConstruct
    public void init() {
        userService.createRoles();
        userService.createAdminAccount();
        try {
//            TODO: привязка к файлам на облачном хранилище
            typeEntityService.createDefaultTypes();
            homeEntityService.createHomeEntity();
        } catch (IOException ignore) {  }
        contactTypeService.createDefaultTypes();
        tagService.createDefaultTypes();
    }
}