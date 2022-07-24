package ru.neonzoff.guidevologdamvc.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.neonzoff.guidevologdamvc.dto.UserDto;
import ru.neonzoff.guidevologdamvc.dto.UserModel;
import ru.neonzoff.guidevologdamvc.service.UserService;
import ru.neonzoff.guidevologdamvc.utils.Pager;

import javax.validation.Valid;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.utils.Converter.convertUserToModel;

/**
 * @author Tseplyaev Dmitry
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final int COUNT_OF_RECORDS = 3;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = {5, 10, 15, 30, 50};

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping
//    public String deleteUser(@RequestParam(required = true, defaultValue = "") Long userId,
//                             @RequestParam(required = true, defaultValue = "") String action,
//                             Model model
//    ) {
//        if (action.equals("delete")) {
//            userService.deleteUser(userId);
//        }
//        return "redirect:/admin";
//    }

    @GetMapping("/users")
    public ModelAndView users(@RequestParam("pageSize") Optional<Integer> pageSize,
                              @RequestParam("page") Optional<Integer> page
    ) {
        ModelAndView model = new ModelAndView("userslist");
        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<UserDto> userPage = userService.findPaginated(PageRequest.of(evalPage, evalPageSize, Sort.by("id").ascending()));
        Pager pager = new Pager(userPage.getTotalPages(), userPage.getNumber(), BUTTONS_TO_SHOW);
        model.addObject("userPage", userPage);
        model.addObject("selectedPageSize", evalPageSize);
        model.addObject("pageSizes", PAGE_SIZES);
        model.addObject("pager", pager);

        return model;
    }

    @GetMapping("/users/{id}")
    public ModelAndView getUser(@PathVariable(name = "id") Long id) {
        ModelAndView model = new ModelAndView("userprofile");
        model.addObject("user", convertUserToModel(userService.findUserById(id)));

        return model;
    }

    @GetMapping("/users/{id}/edit")
    public ModelAndView editUser(@PathVariable(name = "id") Long id) {
        ModelAndView model = new ModelAndView("edituserprofile");
        model.addObject("user", convertUserToModel(userService.findUserById(id)));

        return model;
    }

    @PostMapping("/users/{id}")
    public String editUser(@ModelAttribute("user") @Valid UserModel userModel, BindingResult bindingResult,
                           @PathVariable(name = "id") Long id, Model model
    ) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append('\n');
            }
            model.addAttribute("errorMessage", errorMessage);
        } else {
            if (userService.editUser(userModel)) {
                return "redirect:/admin/users/" + id;
            }
        }
        model.addAttribute("user", convertUserToModel(userService.findUserById(id)));
        return "edituserprofile";
    }


}
