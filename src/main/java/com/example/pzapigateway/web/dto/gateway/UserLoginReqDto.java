package com.example.pzapigateway.web.dto.gateway;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginReqDto {
    private String password;
    private String username;
}
