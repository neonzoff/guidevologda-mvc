package ru.neonzoff.guidevologdamvc.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tseplyaev Dmitry
 */
@Data
public class PropertiesForm {
    private Map<String, String> properties = new HashMap<>();
}
