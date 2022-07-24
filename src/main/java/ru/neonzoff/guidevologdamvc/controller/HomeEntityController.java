package ru.neonzoff.guidevologdamvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import ru.neonzoff.guidevologdamvc.dto.HomeEntityDto;
import ru.neonzoff.guidevologdamvc.service.HomeEntityService;

import javax.validation.Valid;
import java.util.List;

import static ru.neonzoff.guidevologdamvc.utils.Converter.convertHomeEntityToDto;

@Controller
@RequestMapping("homeentity")
public class HomeEntityController {

    private final HomeEntityService homeEntityService;

    public HomeEntityController(HomeEntityService homeEntityService) {
        this.homeEntityService = homeEntityService;
    }

    @GetMapping
    public String get(Model model) {
        model.addAttribute("entity", convertHomeEntityToDto(homeEntityService.get()));
        return "homeentity";
    }

    @GetMapping("images")
    public String getImages(Model model) {
        model.addAttribute("entity", convertHomeEntityToDto(homeEntityService.get()));
        return "homeentityimages";
    }

    @GetMapping("images/upload")
    public String uploadImages() {
        return "uploadhomeentityimages";
    }

    @PostMapping("images/upload")
    public String uploadImages(List<MultipartFile> images, Model model) {
        homeEntityService.uploadImages(images);
        model.addAttribute("entity", convertHomeEntityToDto(homeEntityService.get()));
        return "homeentityimages";
    }

    @GetMapping("edit")
    public String getEditForm(Model model) {
        model.addAttribute("entity", convertHomeEntityToDto(homeEntityService.get()));
        return "edithomeentity";
    }

    @PostMapping("edit")
    public String edit(@Valid HomeEntityDto homeEntityDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(".\n");
            }
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("entity", convertHomeEntityToDto(homeEntityService.get()));
            return "edithomeentity";
        } else {
            homeEntityService.updateWithoutImages(homeEntityDto);
            model.addAttribute("entity", convertHomeEntityToDto(homeEntityService.get()));
            return "redirect:/homeentity";
        }
    }
}
