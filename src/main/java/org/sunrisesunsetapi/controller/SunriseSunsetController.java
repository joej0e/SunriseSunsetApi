package org.sunrisesunsetapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sunrisesunsetapi.dto.SunriseSunsetDto;
import org.sunrisesunsetapi.model.City;
import org.sunrisesunsetapi.service.SunriseSunsetService;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@Validated
@Log4j2
public class SunriseSunsetController {

    final SunriseSunsetService sunriseSunsetService;

    final ObjectMapper mapper;

    @Autowired
    public SunriseSunsetController(SunriseSunsetService sunriseSunsetService, ObjectMapper mapper) {
        this.sunriseSunsetService = sunriseSunsetService;
        this.mapper = mapper;
    }

    @GetMapping("/getAvailableCities")
    public ResponseEntity<List<City>> addCity() {
        return new ResponseEntity<>(sunriseSunsetService.getAvailableCities(), HttpStatus.OK);
    }


    @PostMapping("/addCity/{name}")
    public ResponseEntity<City> addCity(@Valid @Size(min = 2) @PathVariable(value = "name") String name) {
        if (sunriseSunsetService.getCityByName(name).isEmpty()) {
            return new ResponseEntity<>(sunriseSunsetService.addCity(name), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @GetMapping("/sunriseSunset/{name}")
    public ResponseEntity<SunriseSunsetDto> getSunriseSunset(@PathVariable(value = "name") String name) {
        return new ResponseEntity<>(
                sunriseSunsetService.getSunriseSunset(name), HttpStatus.OK);
    }

    @GetMapping("/sunrise/{name}")
    public ResponseEntity<String> getSunrise(@PathVariable(value = "name") String name) {
        return new ResponseEntity<>(sunriseSunsetService.getSunriseSunset(name).getSunrise(), HttpStatus.OK);
    }

    @GetMapping("/sunset/{name}")
    public ResponseEntity<String> getSunset(@PathVariable(value = "name") String name) {
        return new ResponseEntity<>(sunriseSunsetService.getSunriseSunset(name).getSunset(), HttpStatus.OK);
    }


}
