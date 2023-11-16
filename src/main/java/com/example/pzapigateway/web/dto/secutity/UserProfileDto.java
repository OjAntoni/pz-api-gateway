package com.example.pzapigateway.web.dto.secutity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto {
    private long id;
    private String firstname;
    private String lastname;
    private String username;
}
