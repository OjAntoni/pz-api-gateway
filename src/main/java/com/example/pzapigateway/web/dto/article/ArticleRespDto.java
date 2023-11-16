package com.example.pzapigateway.web.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRespDto {

    private UUID id;
    private long author;

    private TopicRespDto topic;
    private String title;
    private List<String> tags;
    private LocalDateTime createdAt;
    private String content;

    private List<URL> images;
}
