package org.sunrisesunsetapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunrisesunsetapi.dto.Sunrise;
import org.sunrisesunsetapi.dto.SunriseSunset;
import org.sunrisesunsetapi.dto.Sunset;
import org.sunrisesunsetapi.model.City;
import org.sunrisesunsetapi.service.SunriseSunsetService;

import java.io.IOException;
import java.util.List;

@RestController
public class SunriseSunsetController {

    @Autowired
    SunriseSunsetService sunriseSunsetService;

    @Autowired
    ObjectMapper mapper;

    @GetMapping("/getAvailableCities")
    public ResponseEntity<List<City>> addCity() {
        return new ResponseEntity<>(sunriseSunsetService.getAvailableCities(), HttpStatus.OK);
    }


    @PostMapping("/addCity/{name}")
    public ResponseEntity<City> addCity(@PathVariable(value = "name") String name) throws IOException {
        if (sunriseSunsetService.getCityByName(name).isEmpty()) {
            return new ResponseEntity<>(sunriseSunsetService.addCity(name), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @GetMapping("/sunrise/{name}")
    public ResponseEntity<Sunrise> getSunrise(@PathVariable(value = "name") String name) throws IOException {
        JsonNode jsonNode = sunriseSunsetService.getSunriseSunset(name).getResults();
        return new ResponseEntity<>(new Sunrise(jsonNode.get("sunrise").textValue()), HttpStatus.OK);
    }

    @GetMapping("/sunset/{name}")
    public ResponseEntity<Sunset> getSunset(@PathVariable(value = "name") String name) throws IOException {
        JsonNode jsonNode = sunriseSunsetService.getSunriseSunset(name).getResults();
        return new ResponseEntity<>(new Sunset(jsonNode.get("sunset").textValue()), HttpStatus.OK);
    }

    @GetMapping("/sunriseSunset/{name}")
    public ResponseEntity<SunriseSunset> getSunriseSunset(@PathVariable(value = "name") String name) throws IOException {
        JsonNode jsonNode = sunriseSunsetService.getSunriseSunset(name).getResults();
        return new ResponseEntity<>(new SunriseSunset(jsonNode.get("sunrise").textValue(), jsonNode.get("sunset").textValue()), HttpStatus.OK);
    }

}
