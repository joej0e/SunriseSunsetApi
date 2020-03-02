package org.sunrisesunsetapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SunriseSunsetDto {
    @JsonProperty
    private String sunrise;
    @JsonProperty
    private String sunset;
}
