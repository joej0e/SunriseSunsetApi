package org.sunrisesunsetapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sunset {
    @JsonProperty
    private String sunset;
}
