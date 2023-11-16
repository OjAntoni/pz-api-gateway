package com.example.pzapigateway.web.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTopicReqDto {
    private long author;
    private String title;
    private String description;
}
