package ru.neonzoff.guidevologdamvc.service;

import ru.neonzoff.guidevologdamvc.domain.Street;

public interface GeoCoderService {
    String getPos(Street street, String houseNumber);
}
