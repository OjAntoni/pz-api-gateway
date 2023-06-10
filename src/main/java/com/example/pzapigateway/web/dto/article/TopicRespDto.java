package com.example.pzapigateway.web.dto.article;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicRespDto {
    private UUID id;
    private UUID author;
    private String title;
    private String description;
}
