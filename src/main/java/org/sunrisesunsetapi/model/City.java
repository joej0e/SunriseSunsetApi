package org.sunrisesunsetapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@NoArgsConstructor
@Getter
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @NotEmpty(message = "Please, provide city name")
    private String city;

    private String lat;

    private String lng;

    public City(String city, String lat, String lng) {
        this.city = city;
        this.lat = lat;
        this.lng = lng;
    }

}
