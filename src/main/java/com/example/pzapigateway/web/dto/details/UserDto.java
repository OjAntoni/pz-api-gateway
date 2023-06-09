package com.example.pzapigateway.web.dto.details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private WorkPlaceDto workPlace;
    private URL urlToImage;
}
