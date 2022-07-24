package ru.neonzoff.guidevologdamvc.service;

import com.amazonaws.services.networkmanager.model.Link;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.neonzoff.guidevologdamvc.domain.Street;
import ru.neonzoff.guidevologdamvc.domain.geocoder.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class GeoCoderServiceImpl implements GeoCoderService {

    private final RestTemplate restTemplate;

    @Value("${yandex.geocoder}")
    private String apiGeoCoderKey;

    @Value("${yandex.url}")
    private String url;

    public GeoCoderServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public String getPos(Street street, String houseNumber) {
        String getUrl = url +
                "apikey=" + apiGeoCoderKey +
                "&format=json" +
                "&geocode=Вологда+" + street.getName() + "+" + houseNumber +
                "&results=1";
        Response object = restTemplate.getForObject(getUrl, Response.class);
        LinkedHashMap response = (LinkedHashMap) object.getAdditionalProperties().get("response");
        LinkedHashMap response1 = (LinkedHashMap) response.get("GeoObjectCollection");
        List geoObjectCollection = (ArrayList) response1.get("featureMember");
        LinkedHashMap geoObject = (LinkedHashMap) geoObjectCollection.get(0);
        LinkedHashMap point = (LinkedHashMap) ((LinkedHashMap) geoObject.get("GeoObject")).get("Point");
        return (String) point.get("pos");
    }
}
