package ru.neonzoff.guidevologdamvc.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ru.neonzoff.guidevologdamvc.dto.UserDto;
import ru.neonzoff.guidevologdamvc.dto.UserModel;
import ru.neonzoff.guidevologdamvc.service.AuditService;
import ru.neonzoff.guidevologdamvc.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static ru.neonzoff.guidevologdamvc.utils.Converter.convertUserToDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertUserToModel;

/**
 * @author Tseplyaev Dmitry
 */
@Controller
@RequestMapping("/")
@PreAuthorize(value = "hasAuthority('USER')")
public class MainController {

    private final UserService userService;

    private final AuditService auditService;

    public MainController(UserService userService, AuditService auditService) {
        this.userService = userService;
        this.auditService = auditService;
    }

    @GetMapping
    public String main(Model model) {
        model.addAttribute("actions", auditService.getActions());
        return "main";
    }

    @GetMapping("profile")
    public ModelAndView profile(HttpServletRequest request) {
        ModelAndView model = new ModelAndView("profile");
        UserDto userDto = convertUserToDto(
                userService.findByUsername(request.getUserPrincipal().getName()).get()
        );
        model.addObject("user", userDto);
        return model;
    }

    @GetMapping("profile/edit")
    public ModelAndView editProfile(HttpServletRequest request) {
        ModelAndView model = new ModelAndView("editprofile");
        UserModel userModel = convertUserToModel(
                userService.findByUsername(request.getUserPrincipal().getName()).get()
        );
        model.addObject("user", userModel);
        return model;
    }

    @PostMapping("profile")
    public String editProfile(@ModelAttribute("user") @Valid UserModel userModel, BindingResult bindingResult,
                              Model model, HttpServletRequest request
    ) throws ServletException {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append('\n');
            }
            model.addAttribute("errorMessage", errorMessage);
            return "editprofile";
        } else {
//            выгоняем юзера для сохранения изменений
            if (userService.editProfile(userModel)) {
                request.logout();
                return "redirect:/";
            } else {
                return "redirect:/profile";
            }
        }
    }

}
