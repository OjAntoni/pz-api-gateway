package com.example.pzapigateway.web.client;

import com.example.pzapigateway.web.dto.activity.CommentReqDto;
import com.example.pzapigateway.web.dto.activity.CommentRespDto;
import com.example.pzapigateway.web.dto.activity.SubscriptionReqDto;
import com.example.pzapigateway.web.dto.activity.SubscriptionRespDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "ENGAGE-HUB-SERVICE")
public interface UserActivityClient {

    @GetMapping("/api/v1/comment")
    List<CommentRespDto> getComments(@RequestParam UUID postId);

    @PostMapping("/api/v1/comment")
    CommentRespDto saveComment(@RequestBody CommentReqDto dto);

    @PutMapping("/api/v1/comment/{id}")
    CommentRespDto updateComment(@PathVariable UUID id, @RequestBody CommentReqDto dto);

    @DeleteMapping("/api/v1/comment/{id}")
    void deleteComment(@PathVariable UUID id);

    @GetMapping("/api/v1/subscription")
    List<SubscriptionRespDto> getSubscriptions(@RequestParam long userId);

    @PostMapping("/api/v1/subscription")
    SubscriptionRespDto saveSubscription(@RequestBody SubscriptionReqDto dto);

    @DeleteMapping("/api/v1/subscription/{id}")
    void deleteSubscription(@PathVariable UUID id);

    @GetMapping("/api/v1/comment/{id}")
    CommentRespDto getComment(@PathVariable UUID id);

    @GetMapping("/api/v1/subscription/{id}")
    SubscriptionRespDto getSubscription(@PathVariable UUID id);

}
