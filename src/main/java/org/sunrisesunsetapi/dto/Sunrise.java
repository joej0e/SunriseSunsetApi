package org.sunrisesunsetapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Sunrise {
    @JsonProperty
    private String sunrise;
}
