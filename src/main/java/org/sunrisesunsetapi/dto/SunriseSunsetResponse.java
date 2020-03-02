package org.sunrisesunsetapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class SunriseSunsetResponse {
    @JsonProperty
    private JsonNode results;
}
