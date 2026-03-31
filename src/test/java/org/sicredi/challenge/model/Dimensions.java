package org.sicredi.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dimensions {
    private Double width;
    private Double height;
    private Double depth;
}

