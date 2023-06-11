package com.example.pzapigateway.web.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRespDto {
    private UUID uuid;
    private LocalDateTime createdAt;
    private UUID authorId;
    private String content;
    private UUID articleId;

}
