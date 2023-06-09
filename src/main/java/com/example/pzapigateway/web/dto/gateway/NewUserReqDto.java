package com.example.pzapigateway.web.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewUserReqDto {
    private String password;
    private String passwordTwo;
    private String name;
    private String surname;
    private String username;
    private String email;
}
