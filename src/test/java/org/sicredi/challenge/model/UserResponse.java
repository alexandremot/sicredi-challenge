package org.sicredi.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    private List<User> users;
    private Integer total;
    private Integer skip;
    private Integer limit;
}