package com.example.pzapigateway.web.dto.details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    private String name;
    private String surname;
    private String username;
    private String email;
}
