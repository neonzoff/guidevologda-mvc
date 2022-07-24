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
import ru.neonzoff.guidevologdamvc.domain.Street;
import ru.neonzoff.guidevologdamvc.dto.StreetDto;
import ru.neonzoff.guidevologdamvc.service.StreetService;
import ru.neonzoff.guidevologdamvc.utils.Pager;

import javax.validation.Valid;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.utils.Constants.DELETING_ERROR_MESSAGE;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertStreetToDto;

/**
 * @author Tseplyaev Dmitry
 */
@Controller
@RequestMapping("/streets")
public class StreetController {
    private static final int COUNT_OF_RECORDS = 3;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = {5, 10, 15, 30, 50};

    private final StreetService streetService;

    public StreetController(StreetService streetService) {
        this.streetService = streetService;
    }

    @GetMapping
    public ModelAndView getAll(@RequestParam("pageSize") Optional<Integer> pageSize,
                               @RequestParam("page") Optional<Integer> page
    ) {
        ModelAndView model = new ModelAndView("streetslist");
        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<StreetDto> streetPage = streetService.findPaginated(PageRequest.of(evalPage, evalPageSize, Sort.by("name").ascending()));
        Pager pager = new Pager(streetPage.getTotalPages(), streetPage.getNumber(), BUTTONS_TO_SHOW);
        model.addObject("streetPage", streetPage);
        model.addObject("selectedPageSize", evalPageSize);
        model.addObject("pageSizes", PAGE_SIZES);
        model.addObject("pager", pager);

        return model;
    }

    @GetMapping("/new")
    public String getAddForm(Model model) {
        StreetDto streetDto = new StreetDto();
        model.addAttribute("streetForm", streetDto);
        return "addstreet";
    }

    @PostMapping("/new")
    public String createTag(@Valid @ModelAttribute StreetDto streetDto, BindingResult bindingResult,
                            Model model
    ) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append('\n');
            }
            model.addAttribute("errorMessage", errorMessage);
            return "addstreet";
        } else {
            streetService.saveStreet(streetDto);
            return "redirect:/streets";
        }
    }

    @GetMapping("{id}")
    public String getById(@PathVariable Long id, Model model) {
        model.addAttribute("street", convertStreetToDto(streetService.findById(id)));
        return "street";
    }

    @GetMapping("{id}/delete")
    public String removeById(@PathVariable Long id, Model model) {
        if (!streetService.deleteStreet(convertStreetToDto(streetService.findById(id)))) {
            Street street = streetService.findById(id);
            model.addAttribute("street", convertStreetToDto(street));
            model.addAttribute("error", String.format(DELETING_ERROR_MESSAGE, street.getEntities().size()));
            return "street";
        }
        return "redirect:/streets";
    }
}
