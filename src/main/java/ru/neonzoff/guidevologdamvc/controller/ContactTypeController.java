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
import ru.neonzoff.guidevologdamvc.domain.ContactType;
import ru.neonzoff.guidevologdamvc.dto.ContactTypeDto;
import ru.neonzoff.guidevologdamvc.service.ContactTypeService;
import ru.neonzoff.guidevologdamvc.utils.Pager;

import javax.validation.Valid;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.utils.Constants.DELETING_ERROR_MESSAGE;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertContactTypeToDto;

/**
 * @author Tseplyaev Dmitry
 */
@Controller
@RequestMapping("/contacttypes")
public class ContactTypeController {
    private static final int COUNT_OF_RECORDS = 3;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = {5, 10, 15, 30, 50};

    private final ContactTypeService contactTypeService;

    public ContactTypeController(ContactTypeService contactTypeService) {
        this.contactTypeService = contactTypeService;
    }

    @GetMapping
    public ModelAndView getAll(@RequestParam("pageSize") Optional<Integer> pageSize,
                               @RequestParam("page") Optional<Integer> page
    ) {
        ModelAndView model = new ModelAndView("contacttypeslist");
        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<ContactTypeDto> contactTypePage = contactTypeService.findPaginated(PageRequest.of(evalPage, evalPageSize, Sort.by("name").ascending()));
        Pager pager = new Pager(contactTypePage.getTotalPages(), contactTypePage.getNumber(), BUTTONS_TO_SHOW);
        model.addObject("contactTypePage", contactTypePage);
        model.addObject("selectedPageSize", evalPageSize);
        model.addObject("pageSizes", PAGE_SIZES);
        model.addObject("pager", pager);

        return model;
    }

    @GetMapping("/new")
    public String getAddForm(Model model) {
        ContactTypeDto contactTypeDto = new ContactTypeDto();
        model.addAttribute("contactTypeDtoForm", contactTypeDto);
        return "addcontacttype";
    }

    @PostMapping("/new")
    public String createTag(@Valid @ModelAttribute ContactTypeDto contactTypeDto, BindingResult bindingResult,
                            Model model
    ) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append('\n');
            }
            model.addAttribute("errorMessage", errorMessage);
            return "addcontacttypes";
        } else {
            contactTypeService.saveContactType(contactTypeDto);
            return "redirect:/contacttypes";
        }
    }

    @GetMapping("{id}")
    public String getById(@PathVariable Long id, Model model) {
        model.addAttribute("contactType", convertContactTypeToDto(contactTypeService.findById(id)));
        return "contacttype";
    }

    @GetMapping("{id}/delete")
    public String deleteById(@PathVariable Long id, Model model) {
        ContactType contactType = contactTypeService.findById(id);
        if (!contactTypeService.deleteContactType(convertContactTypeToDto(contactType))) {
            model.addAttribute("contactType", convertContactTypeToDto(contactType));
            model.addAttribute("error", String.format(DELETING_ERROR_MESSAGE, contactType.getContacts().size()));
            return "contacttype";
        }
        return "redirect:/contacttypes";
    }
}
