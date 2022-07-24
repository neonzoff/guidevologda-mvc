package ru.neonzoff.guidevologdamvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.neonzoff.guidevologdamvc.dto.BaseEntityDto;
import ru.neonzoff.guidevologdamvc.dto.TypeEntityDto;
import ru.neonzoff.guidevologdamvc.service.BaseEntityService;
import ru.neonzoff.guidevologdamvc.service.TagService;
import ru.neonzoff.guidevologdamvc.service.TypeEntityService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("rest")
public class FitRestController {

    private final TypeEntityService typeEntityService;
    private final BaseEntityService baseEntityService;
    private final TagService tagService;

    public FitRestController(TypeEntityService typeEntityService, BaseEntityService baseEntityService, TagService tagService) {
        this.typeEntityService = typeEntityService;
        this.baseEntityService = baseEntityService;
        this.tagService = tagService;
    }

    @GetMapping("typesofentities")
    public List<TypeEntityDto> getAllByRest() {
        return typeEntityService.findAll();
    }

    @GetMapping("entities")
    public List<BaseEntityDto> getEntities(@RequestParam(required = false) List<String> tags, @RequestParam(required = false) Long typeEntityId) {
        Set<BaseEntityDto> entities = new HashSet<>(baseEntityService.findByActive(true));
        if (tags != null) {
            tags.forEach(tag -> {
                var foundedTag = tagService.findByName(tag);
                entities.retainAll(baseEntityService.findByTag(foundedTag));
            });
        }
        if (typeEntityId != null) {
            var typeEntity = typeEntityService.findById(typeEntityId);
            entities.retainAll(baseEntityService.findByTypeEntity(typeEntity));
        }
        if (tags == null && typeEntityId == null) {
            return baseEntityService.findAll();
        }
        return new ArrayList<>(entities);
    }

}
