package com.example.pzapigateway.web.dto.details;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatchUserDto {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private PatchWorkPlaceDto workPlace;
    private URL urlToImage;
}
