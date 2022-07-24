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
import ru.neonzoff.guidevologdamvc.domain.Tag;
import ru.neonzoff.guidevologdamvc.dto.TagDto;
import ru.neonzoff.guidevologdamvc.service.TagService;
import ru.neonzoff.guidevologdamvc.utils.Pager;

import javax.validation.Valid;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.utils.Constants.DELETING_ERROR_MESSAGE;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertTagToDto;

/**
 * @author Tseplyaev Dmitry
 */
@Controller
@RequestMapping("/tags")
public class TagController {
    private static final int COUNT_OF_RECORDS = 3;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = {5, 10, 15, 30, 50};

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ModelAndView getAll(@RequestParam("pageSize") Optional<Integer> pageSize,
                               @RequestParam("page") Optional<Integer> page
    ) {
        ModelAndView model = new ModelAndView("tagslist");
        // Evaluate page size. If requested parameter is null, return initial
        // page size
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        // Evaluate page. If requested parameter is null or less than 0 (to
        // prevent exception), return initial size. Otherwise, return value of
        // param. decreased by 1.
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<TagDto> tagPage = tagService.findPaginated(PageRequest.of(evalPage, evalPageSize, Sort.by("name").ascending()));
        Pager pager = new Pager(tagPage.getTotalPages(), tagPage.getNumber(), BUTTONS_TO_SHOW);
        model.addObject("tagPage", tagPage);
        model.addObject("selectedPageSize", evalPageSize);
        model.addObject("pageSizes", PAGE_SIZES);
        model.addObject("pager", pager);

        return model;
    }

    @GetMapping("/new")
    public String getAddForm(Model model) {
        TagDto tagDto = new TagDto();
        model.addAttribute("tagForm", tagDto);
        return "addtag";
    }

    @PostMapping("/new")
    public String createTag(@Valid @ModelAttribute TagDto tagDto, BindingResult bindingResult,
                            Model model
    ) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append('\n');
            }
            model.addAttribute("errorMessage", errorMessage);
            return "addtag";
        } else {
            tagService.saveTag(tagDto);
            return "redirect:/tags";
        }
    }

    @GetMapping("{id}")
    public String getById(@PathVariable Long id, Model model) {
        model.addAttribute("tag", convertTagToDto(tagService.findById(id)));
        return "tag";
    }

    @GetMapping("{id}/delete")
    public String deleteById(@PathVariable Long id, Model model) {
        Tag tag = tagService.findById(id);
        if (!tagService.deleteTag(convertTagToDto(tag))) {
            model.addAttribute("tag", convertTagToDto(tagService.findById(id)));
            model.addAttribute("error", String.format(DELETING_ERROR_MESSAGE, tag.getEntities().size()));
            return "tag";
        }
        return "redirect:/tags";
    }
}
