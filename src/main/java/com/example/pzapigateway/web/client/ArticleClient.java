package com.example.pzapigateway.web.client;

import com.example.pzapigateway.web.dto.article.ArticleRespDto;
import com.example.pzapigateway.web.dto.article.NewArticleReqDto;
import com.example.pzapigateway.web.dto.article.NewTopicReqDto;
import com.example.pzapigateway.web.dto.article.TopicRespDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "article-client", url = "${article-client.url}")
public interface ArticleClient {

    @PostMapping("/api/v1/article")
    ArticleRespDto saveArticle(@RequestBody NewArticleReqDto dto);

    @PutMapping("/api/v1/article/{id}")
    ArticleRespDto updateArticle(@RequestBody ArticleRespDto dto, @PathVariable UUID id);

    @DeleteMapping("/api/v1/article/{id}")
    void deleteArticle(@PathVariable UUID id);

    @GetMapping("/api/v1/article/search")
    List<ArticleRespDto> searchArticle(
            @RequestParam(required = false) UUID author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    );

    @GetMapping("/api/v1/topic/{id}")
    TopicRespDto getTopic(@PathVariable UUID id);

    @PostMapping("/api/v1/topic")
    TopicRespDto saveTopic(@RequestBody NewTopicReqDto dto);

    @GetMapping("/api/v1/topic/all")
    List<TopicRespDto> getAllTopics();

    @GetMapping("/api/v1/topic/all_by_title")
    List<TopicRespDto> getAllTopics(@RequestParam String title);

    @PutMapping("/api/v1/topic/{id}")
    TopicRespDto updateTopic(@RequestBody NewTopicReqDto dto, @PathVariable UUID id);

    @DeleteMapping("/api/v1/topic/{id}")
    void deleteTopic(@PathVariable UUID id);

    @GetMapping("/api/v1/article/all/{id}")
    List<ArticleRespDto> getArticles(@PathVariable(name = "id") UUID userId);

    @GetMapping("/api/v1/topic/all/{id}")
    List<TopicRespDto> getTopics(@PathVariable(name = "id") UUID userId);

    @GetMapping("/api/v1/article/{id}")
    ArticleRespDto getArticle(@PathVariable UUID id);
}
