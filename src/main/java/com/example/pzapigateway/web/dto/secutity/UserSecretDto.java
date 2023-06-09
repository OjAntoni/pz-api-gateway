package com.example.pzapigateway.web.dto.secutity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserSecretDto {
    private UUID id;
    private String password;
    private String role;
    private UUID detailsId;
}
