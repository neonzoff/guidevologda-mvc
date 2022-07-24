package ru.neonzoff.guidevologdamvc.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ru.neonzoff.guidevologdamvc.domain.BaseEntity;
import ru.neonzoff.guidevologdamvc.domain.Contact;
import ru.neonzoff.guidevologdamvc.domain.ContactType;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityDto;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityModel;
import ru.neonzoff.guidevologdamvc.dto.ContactDto;
import ru.neonzoff.guidevologdamvc.dto.PropertiesForm;
import ru.neonzoff.guidevologdamvc.dto.StreetDto;
import ru.neonzoff.guidevologdamvc.dto.TagDto;
import ru.neonzoff.guidevologdamvc.dto.TypeEntityDto;
import ru.neonzoff.guidevologdamvc.service.BaseEntityService;
import ru.neonzoff.guidevologdamvc.service.ContactService;
import ru.neonzoff.guidevologdamvc.service.ContactTypeService;
import ru.neonzoff.guidevologdamvc.service.StreetService;
import ru.neonzoff.guidevologdamvc.service.TagService;
import ru.neonzoff.guidevologdamvc.service.TypeEntityService;
import ru.neonzoff.guidevologdamvc.utils.Pager;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ru.neonzoff.guidevologdamvc.utils.Constants.DELETING_ERROR_MESSAGE;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertBaseEntityToDto;
import static ru.neonzoff.guidevologdamvc.utils.Converter.convertBaseEntityToModel;

/**
 * @author Tseplyaev Dmitry
 */
@Controller
@RequestMapping("/entities")
public class EntityController {
    private static final int COUNT_OF_RECORDS = 3;
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private static final int[] PAGE_SIZES = {5, 10, 15, 30, 50};

    private final BaseEntityService baseEntityService;

    private final StreetService streetService;

    private final TypeEntityService typeEntityService;

    private final ContactTypeService contactTypeService;

    private final ContactService contactService;

    private final TagService tagService;

    public EntityController(BaseEntityService baseEntityService, StreetService streetService,
                            TypeEntityService typeEntityService, ContactTypeService contactTypeService,
                            ContactService contactService, TagService tagService
    ) {
        this.baseEntityService = baseEntityService;
        this.streetService = streetService;
        this.typeEntityService = typeEntityService;
        this.contactTypeService = contactTypeService;
        this.contactService = contactService;
        this.tagService = tagService;
    }

    @GetMapping
    public ModelAndView getAll(@RequestParam("pageSize") Optional<Integer> pageSize,
                               @RequestParam("page") Optional<Integer> page
    ) {
        ModelAndView model = new ModelAndView("entitieslist");
        int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
        int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
        Page<BaseEntityModel> baseEntityPage = baseEntityService.findPaginated(PageRequest.of(evalPage, evalPageSize, Sort.by("name").ascending()));
        Pager pager = new Pager(baseEntityPage.getTotalPages(), baseEntityPage.getNumber(), BUTTONS_TO_SHOW);
        model.addObject("entityPage", baseEntityPage);
        model.addObject("selectedPageSize", evalPageSize);
        model.addObject("pageSizes", PAGE_SIZES);
        model.addObject("pager", pager);

        return model;
    }

    @GetMapping("/new")
    public String getAddForm(Model model) {
        BaseEntityModel baseEntityModel = new BaseEntityModel();
        List<StreetDto> streets = streetService.findAll("name");
        List<TypeEntityDto> typesOfEntity = typeEntityService.findAll("name");
        List<TagDto> tags = tagService.findAll("name");

        model.addAttribute("entityForm", baseEntityModel);
        model.addAttribute("streets", streets);
        model.addAttribute("typesOfEntity", typesOfEntity);
        model.addAttribute("tags", tags);
        return "addentity";
    }

    @PostMapping("/new")
    public String createEntity(@Valid @ModelAttribute BaseEntityDto baseEntityDto,
                               BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(".\n");
            }

            BaseEntityModel baseEntityModel = new BaseEntityModel();
            List<StreetDto> streets = streetService.findAll("name");
            List<TypeEntityDto> typesOfEntity = typeEntityService.findAll("name");
            List<TagDto> tags = tagService.findAll("name");

            model.addAttribute("entityForm", baseEntityModel);
            model.addAttribute("streets", streets);
            model.addAttribute("typesOfEntity", typesOfEntity);
            model.addAttribute("tags", tags);
            model.addAttribute("errorMessage", errorMessage);

            return "addentity";
        } else {
            baseEntityService.saveBaseEntity(baseEntityDto);
            return "redirect:/entities/" + baseEntityService.findByName(baseEntityDto.getName()).getId() + "/images/new";
        }
    }

    @GetMapping("/{id}/images/new")
    public String addImages(@PathVariable Long id, Model model) {
        BaseEntity baseEntity = baseEntityService.findById(id);
        model.addAttribute("entity", baseEntity);
        return "addimages";
    }

    @PostMapping("/{id}/images")
    public String editImages(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        baseEntityService.addImagesToEntity(id, images);
        return "redirect:/entities/" + id + "/contacts/new";
    }

    @GetMapping("/{id}/images/upload")
    public String addImagesNew(@PathVariable Long id, Model model) {
        BaseEntity baseEntity = baseEntityService.findById(id);
        model.addAttribute("entity", baseEntity);
        return "editimages";
    }

    @PostMapping("/{id}/images/upload")
    public String editImagesNew(@PathVariable Long id, @RequestParam("images") List<MultipartFile> images) {
        baseEntityService.addImagesToEntity(id, images);
        return "redirect:/entities/" + id + "/images";
    }

    @GetMapping("/{id}/contacts/new")
    public String addContacts(@PathVariable Long id, Model model) {
        BaseEntity baseEntity = baseEntityService.findById(id);
        List<ContactType> contactTypesDto = contactTypeService.findAll();
        PropertiesForm form = new PropertiesForm();
        Map<String, String> properties = new HashMap<>();
        for (ContactType contactType : contactTypesDto) {
            properties.put(contactType.getName(), " ");
        }
        form.setProperties(properties);
        model.addAttribute("entity", convertBaseEntityToDto(baseEntity));
        model.addAttribute("contactsForm", form);

        return "addcontacts";
    }

    @PostMapping("/{id}/contacts")
    @Transactional
    public String editContacts(@PathVariable Long id, @ModelAttribute("form") PropertiesForm form) {
        BaseEntity baseEntity = baseEntityService.findById(id);
//        удаляем старые контакты
        for (Contact contact : baseEntity.getContacts()) {
            contactService.deleteContact(contact);
            for (ContactType contactType : contactTypeService.findAll()) {
                contactType.getContacts().remove(contact);
            }
        }

        for (Map.Entry<String, String> entry : form.getProperties().entrySet()) {
            ContactType contactType = contactTypeService.findByName(entry.getKey());
            if (!entry.getValue().trim().isEmpty()) {
                ContactDto contactDto = new ContactDto();
                contactDto.setValue(entry.getValue().trim());
                Contact createdContact = contactService.findById(contactService.saveContact(contactDto));
                contactType.getContacts().add(createdContact);
                baseEntity.getContacts().add(createdContact);
            }
        }

        return "redirect:/entities";
    }

    @GetMapping("/{id}/contacts")
    public ModelAndView getContacts(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("contacts");
        BaseEntity baseEntity = baseEntityService.findById(id);
        PropertiesForm form = new PropertiesForm();
        Map<String, String> properties = new HashMap<>();
        for (Contact contact : baseEntity.getContacts()) {
            properties.put(contactTypeService.findByContacts(contact).getName(), contact.getValue());
        }
        form.setProperties(properties);
        modelAndView.addObject("entity", convertBaseEntityToDto(baseEntity));
        modelAndView.addObject("contactsForm", form);

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView getEntity(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("entity");
        BaseEntity entity = baseEntityService.findById(id);
        modelAndView.addObject("entity", entity);

        return modelAndView;
    }

    @GetMapping("/{id}/images")
    public ModelAndView getEntityImages(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("entityimages");
        BaseEntity entity = baseEntityService.findById(id);
        modelAndView.addObject("entity", entity);

        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView getEditForm(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("editentity");
        BaseEntity entity = baseEntityService.findById(id);
        BaseEntityModel baseEntityModel = convertBaseEntityToModel(entity);
        List<StreetDto> streets = streetService.findAll("name");
        List<TypeEntityDto> typesOfEntity = typeEntityService.findAll("name");
        List<TagDto> tags = tagService.findAll("name");

        modelAndView.addObject("entityForm", baseEntityModel);
        modelAndView.addObject("streets", streets);
        modelAndView.addObject("typesOfEntity", typesOfEntity);
        modelAndView.addObject("tags", tags);

        return modelAndView;
    }

    @PostMapping("/{id}")
    public String editEntity(@PathVariable Long id, @Valid @ModelAttribute BaseEntityDto baseEntityDto,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessage.append(error.getDefaultMessage()).append(".\n");
            }

            BaseEntityModel baseEntityModel = convertBaseEntityToModel(baseEntityService.findById(id));
            List<StreetDto> streets = streetService.findAll("name");
            List<TypeEntityDto> typesOfEntity = typeEntityService.findAll("name");
            List<TagDto> tags = tagService.findAll("name");

            model.addAttribute("entityForm", baseEntityModel);
            model.addAttribute("streets", streets);
            model.addAttribute("typesOfEntity", typesOfEntity);
            model.addAttribute("tags", tags);
            model.addAttribute("errorMessage", errorMessage);

            return "editentity";
        } else {
            baseEntityService.saveBaseEntity(baseEntityDto);
            return "redirect:/entities/" + id;
        }
    }

    @GetMapping("{id}/delete")
    public String deleteEntity(@PathVariable Long id, Model model) {
        BaseEntity entity = baseEntityService.findById(id);
        if (!baseEntityService.deleteBaseEntity(convertBaseEntityToDto(entity))) {
            model.addAttribute("error", String.format(DELETING_ERROR_MESSAGE, entity.getTracks().size()));
            model.addAttribute("entity", entity);
            return "entity";
        }
        return "redirect:/entities";
    }
}
