package com.example.pzapigateway.web.dto.secutity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NewUserSecretReqDto {
    private UUID detailsId;
    private String password;
    private String passwordTwo;

}
