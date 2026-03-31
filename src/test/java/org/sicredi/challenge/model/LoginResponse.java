package org.sicredi.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
    private Integer id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String accessToken;
    private String refreshToken;
}