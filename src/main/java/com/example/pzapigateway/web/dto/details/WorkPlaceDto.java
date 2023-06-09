package com.example.pzapigateway.web.dto.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkPlaceDto {
    private long id;
    private String faculty;
    private String specialization;
}
