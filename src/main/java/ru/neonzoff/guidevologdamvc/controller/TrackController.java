package ru.neonzoff.guidevologdamvc.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.neonzoff.guidevologdamvc.domain.Track;
import ru.neonzoff.guidevologdamvc.domain.TypeEntity;
import ru.neonzoff.guidevologdamvc.dto.TrackDto;
import ru.neonzoff.guidevologdamvc.dto.TrackModel;
import ru.neonzoff.guidevologdamvc.service.TrackService;
import ru.neonzoff.guidevologdamvc.utils.Pager;

import javax.validation.Valid;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.utils.Constants.DELETING_ERROR_MESSAGE;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertTrackToDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertTypeEntityToDto;

@Controller
@RequestMapping("tracks")
public class TrackController {

    private static final int COUNT_OF_RECORDS = 3;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = {5, 10, 15, 30, 50};

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public ModelAndView getAll(@RequestParam("pageSize") Optional<Integer> pageSize,
                               @RequestParam("page") Optional<Integer> page
    ) {
        ModelAndView model = new ModelAndView("trackslist");
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<TrackDto> trackPage = trackService.findPaginated(PageRequest.of(evalPage, evalPageSize, Sort.by("name").ascending()));
        Pager pager = new Pager(trackPage.getTotalPages(), trackPage.getNumber(), BUTTONS_TO_SHOW);
        model.addObject("trackPage", trackPage);
        model.addObject("selectedPageSize", evalPageSize);
        model.addObject("pageSizes", PAGE_SIZES);
        model.addObject("pager", pager);

        return model;
    }

    @GetMapping("new")
    public String getForm(Model model) {
        TrackModel trackModel = new TrackModel();
        model.addAttribute("track", trackModel);

        return "addtrack";
    }

    @PostMapping("new")
    public String createTrack(@Valid TrackModel trackModel, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append('\n');
            }
            model.addAttribute("errorMessage", errorMessage);
            return "addtrack";
        } else {
            trackService.saveTrack(trackModel);
            return "redirect:/tracks";
        }
    }

    @GetMapping("{id}")
    public String getTrackById(@PathVariable Long id, Model model) {
        model.addAttribute("track", convertTrackToDto(trackService.findById(id)));
        return "track";
    }

    @GetMapping("{id}/delete")
    public String deleteTrackById(@PathVariable Long id, Model model) {
        Track track = trackService.findById(id);
        if (!trackService.deleteTrack(track)) {
            model.addAttribute("track", convertTrackToDto(trackService.findById(id)));
            model.addAttribute("error", String.format(DELETING_ERROR_MESSAGE, track.getEntities().size()));
            return "track";
        }
        return "redirect:/tracks";
    }
}
