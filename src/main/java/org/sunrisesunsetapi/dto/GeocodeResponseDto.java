package org.sunrisesunsetapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GeocodeResponseDto {

    @JsonProperty
    private String lat;

    @JsonProperty
    private String lon;

    @JsonProperty
    private String type;

}
