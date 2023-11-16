package com.example.pzapigateway.web.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRespDto {
    private UUID id;
    private long userId;
    private UUID topicId;
}
