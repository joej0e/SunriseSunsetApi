package org.sunrisesunsetapi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunrisesunsetapi.dto.GeocodeResponseDto;
import org.sunrisesunsetapi.dto.SunriseSunsetDto;
import org.sunrisesunsetapi.exception.CityNotFoundException;
import org.sunrisesunsetapi.exception.LocationTypeException;
import org.sunrisesunsetapi.model.City;
import org.sunrisesunsetapi.repository.CityRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;


@Service
@Log4j2
public class SunriseSunsetService {

    private final CityRepository cityRepository;

    private final ObjectMapper mapper;

    @Value("${urls.CITIES_URL}")
    private String citiesUrl;

    @Value("${urls.GEOCODE_URL}")
    private String geocodeUrl;

    @Value("${urls.SUNRISE_SUNSET_URL}")
    private String sunriseSunsetUrl;

    @Autowired
    public SunriseSunsetService(CityRepository cityRepository, ObjectMapper mapper) {
        this.cityRepository = cityRepository;
        this.mapper = mapper;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Transactional
    @PostConstruct
    @Scheduled(cron = "0 15 05 00 * ?")
    public void saveCities() {
        URL url = null;
        try {
            url = new URL(citiesUrl);
        } catch (MalformedURLException e) {
            log.error("CITIES_URL property in application.yml is incorrect", e);
        }
        HttpURLConnection con = null;
        List<City> cities = null;
        if (url == null) {
            throw new NullPointerException("url is null");
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            TypeReference<List<City>> typeReference = new TypeReference<>() {
            };
            try (InputStream inputStream = con.getInputStream()) {
                cities = mapper.readValue(inputStream, typeReference);
            } catch (IOException e) {
                log.error("Error has occurred while getting list of cities", e);
            }
        } catch (IOException e) {
            log.error("Can't connect to resource with cities", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        if (cities == null) {
            throw new NullPointerException("cities is null");
        }
        cityRepository.saveAll(cities);
    }

    private GeocodeResponseDto getCoordinates(String cityName) {
        URL url = null;
        try {
            url = new URL(geocodeUrl + "&city=" + cityName);
        } catch (MalformedURLException e) {
            log.error("GEOCODE_URL property in application.yml is incorrect", e);
        }
        GeocodeResponseDto geocodeResponseDto = null;
        HttpURLConnection con = null;
        if (url == null) {
            throw new NullPointerException("url is null");
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            try (InputStream inputStream = con.getInputStream()) {
                geocodeResponseDto = mapper.readValue(inputStream, GeocodeResponseDto[].class)[0];
                String type = geocodeResponseDto.getType();
                if (!(type.equals("city") || type.equals("town")
                        || type.equals("village") || type.equals("administrative"))) {
                    throw new LocationTypeException("Please, provide place that have type of: city, town, village, administrative");
                }
            } catch (IOException e) {
                log.error("Error has occurred while getting coordinates", e);
            }
        } catch (IOException e) {
            log.error("Can't connect to Geocode API", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        if (geocodeResponseDto == null) {
            throw new NullPointerException("geocodeResponse is null");
        }
        return geocodeResponseDto;
    }

    public SunriseSunsetDto getSunriseSunset(String cityName) {
        City city = cityRepository.getCityByCity(cityName).orElseThrow(() -> new CityNotFoundException("City with name" + cityName + "not found"));
        URL url = null;
        try {
            url = new URL(sunriseSunsetUrl + "lat=" + city.getLat() + "&lng=" + city.getLng());
        } catch (MalformedURLException e) {
            log.error("SUNRISE_SUNSET_URL property in application.yml is incorrect", e);
        }
        HttpURLConnection con = null;
        JsonNode jsonNode = null;
        if (url == null) {
            throw new NullPointerException("url is null");
        }
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            try (InputStream inputStream = con.getInputStream()) {
                jsonNode = mapper.readValue(inputStream, JsonNode.class).get("results");
            }
        } catch (IOException e) {
            log.error("Can't connect to Sunrise-Sunset API", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        if (jsonNode == null) {
            throw new NullPointerException("cities is null");
        }
        return new SunriseSunsetDto(jsonNode.get("sunrise").textValue(), jsonNode.get("sunset").textValue());
    }

    public City addCity(String cityName) {
        GeocodeResponseDto geocodeResponseDto = getCoordinates(cityName);
        City city = new City(cityName, geocodeResponseDto.getLat(), geocodeResponseDto.getLon());
        return cityRepository.save(city);
    }

    public List<City> getAvailableCities() {
        return cityRepository.findAll();
    }

    public Optional<City> getCityByName(String name) {
        return cityRepository.getCityByCity(name);
    }
}
