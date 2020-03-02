package org.sunrisesunsetapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunrisesunsetapi.dto.GeocodeResponse;
import org.sunrisesunsetapi.dto.SunriseSunsetResponse;
import org.sunrisesunsetapi.model.City;
import org.sunrisesunsetapi.repository.CityRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;


@Service
@Log4j2
public class SunriseSunsetService {

    private final CityRepository cityRepository;

    private final ObjectMapper mapper;

    public static final String CITIES_URL = "https://simplemaps.com/static/data/country-cities/ua/ua.json";
    public static final String GEOCODE_URL = "https://eu1.locationiq.com/v1/search.php?" +
            "key=bacf3d0fed499b&countrycode=ua&country=ukraine&limit=1&format=json";
    public static final String SUNRISE_SUNSET_URL = "https://api.sunrise-sunset.org/json?";

    @Autowired
    public SunriseSunsetService(CityRepository cityRepository, ObjectMapper mapper) {
        this.cityRepository = cityRepository;
        this.mapper = mapper;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Transactional
    @PostConstruct
    @Scheduled(cron = "0 15 05 00 * ?")
    public void saveCities() throws IOException {
        URL url = new URL(CITIES_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        TypeReference<List<City>> typeReference = new TypeReference<>() {
        };
        List<City> cities = mapper.readValue(con.getInputStream(), typeReference);
        cityRepository.saveAll(cities);
    }

    private GeocodeResponse getCoordinates(String cityName) throws IOException {
        URL url = new URL(GEOCODE_URL + "&city=" + cityName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(con.getInputStream(), GeocodeResponse[].class)[0];
    }

    public SunriseSunsetResponse getSunriseSunset(String cityName) throws IOException {
        City city = cityRepository.getCityByCity(cityName).orElseThrow();
        URL url = new URL(SUNRISE_SUNSET_URL + "lat=" + city.getLat() + "&lng=" + city.getLng());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        return mapper.readValue(con.getInputStream(), SunriseSunsetResponse.class);
    }

    public City addCity(String cityName) throws IOException {
        GeocodeResponse geocodeResponse = getCoordinates(cityName);
        City city = new City(cityName, geocodeResponse.getLat(), geocodeResponse.getLon());
        return cityRepository.save(city);
    }

    public List<City> getAvailableCities() {
        return cityRepository.findAll();
    }

    public Optional<City> getCityByName(String name) {
        return cityRepository.getCityByCity(name);
    }
}
