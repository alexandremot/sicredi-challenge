package org.sicredi.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {
    private Integer rating;
    private String comment;
    private String date;
    private String reviewerName;
    private String reviewerEmail;
}

