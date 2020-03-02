package org.sunrisesunsetapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SunriseSunset {
    @JsonProperty
    private String sunrise;
    @JsonProperty
    private String sunset;
}
