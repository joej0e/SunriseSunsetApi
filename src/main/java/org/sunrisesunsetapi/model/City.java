package org.sunrisesunsetapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String city;

    private String lat;

    private String lng;

    public City(String city, String lat, String lng) {
        this.city = city;
        this.lat = lat;
        this.lng = lng;
    }

}
