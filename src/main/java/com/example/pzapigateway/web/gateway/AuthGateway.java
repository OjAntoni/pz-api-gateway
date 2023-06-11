package com.example.pzapigateway.web.gateway;

import com.example.pzapigateway.exception.BadRequestException;
import com.example.pzapigateway.exception.ForbiddenRequestException;
import com.example.pzapigateway.exception.NoContentException;
import com.example.pzapigateway.security.JwtTokenProvider;
import com.example.pzapigateway.service.UserService;
import com.example.pzapigateway.web.client.ArticleClient;
import com.example.pzapigateway.web.client.UserActivityClient;
import com.example.pzapigateway.web.client.UserDetailsClient;
import com.example.pzapigateway.web.client.UserSecurityClient;
import com.example.pzapigateway.web.dto.activity.CommentReqDto;
import com.example.pzapigateway.web.dto.activity.CommentRespDto;
import com.example.pzapigateway.web.dto.activity.SubscriptionReqDto;
import com.example.pzapigateway.web.dto.activity.SubscriptionRespDto;
import com.example.pzapigateway.web.dto.article.ArticleRespDto;
import com.example.pzapigateway.web.dto.article.NewArticleReqDto;
import com.example.pzapigateway.web.dto.article.NewTopicReqDto;
import com.example.pzapigateway.web.dto.article.TopicRespDto;
import com.example.pzapigateway.web.dto.details.NewUserLocationResp;
import com.example.pzapigateway.web.dto.details.PatchUserDto;
import com.example.pzapigateway.web.dto.details.UserDto;
import com.example.pzapigateway.web.dto.details.UserRegistrationDto;
import com.example.pzapigateway.web.dto.gateway.NewUserReqDto;
import com.example.pzapigateway.web.dto.gateway.UserLoginReqDto;
import com.example.pzapigateway.web.dto.secutity.NewUserSecretReqDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pl/edu/pw/portal")
@AllArgsConstructor
public class AuthGateway {
    private UserDetailsClient userDetailsClient;
    private UserSecurityClient userSecurityClient;
    private JwtTokenProvider tokenProvider;
    private UserService userService;
    private ArticleClient articleClient;
    private UserActivityClient activityClient;

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody NewUserReqDto dto) {
        UserRegistrationDto detailsDto = UserRegistrationDto.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .build();

        NewUserLocationResp newUserLocationResp;
        try {
            newUserLocationResp = userDetailsClient.saveUserDetails(detailsDto);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }


        NewUserSecretReqDto secretDto = NewUserSecretReqDto.builder()
                .password(dto.getPassword())
                .passwordTwo(dto.getPasswordTwo())
                .detailsId(newUserLocationResp.getLocation())
                .build();

        try {
            userSecurityClient.saveUserSecret(secretDto);
        } catch (BadRequestException e) {
            userDetailsClient.deleteUserDetails(newUserLocationResp.getLocation());
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        } catch (ForbiddenRequestException e) {
            userDetailsClient.deleteUserDetails(newUserLocationResp.getLocation());
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody UserLoginReqDto dto) {
        try {
            String jwtToken = userSecurityClient.getJwtToken(dto);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (BadRequestException | ForbiddenRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user")
    ResponseEntity<?> userById(@RequestParam(required = false) UUID id, HttpServletRequest req) {
        String jwt = tokenProvider.resolveToken(req);
        UUID selfId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (selfId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        UserDto byId = userDetailsClient.getUserDetailsById(id == null ? selfId : id);
        return new ResponseEntity<>(byId, HttpStatus.OK);
    }

    @PutMapping("/user")
    ResponseEntity<?> updateUserById(HttpServletRequest req, @RequestBody PatchUserDto dto) {
        try {
            String jwt = tokenProvider.resolveToken(req);
            UUID id = userService.getDetailsUUIDBySecretUUID(jwt);
            if(id==null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            userDetailsClient.updateUserDetailsById(id, dto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user")
    ResponseEntity<Void> deleteUserById(HttpServletRequest req) {
        try {
            userSecurityClient.deleteUserSecret("Bearer " + tokenProvider.resolveToken(req));
        } catch (ForbiddenRequestException e){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/user/all")
    ResponseEntity<List<UserDto>> getAllUsers(@RequestParam int page, @RequestParam int size) {
        List<UserDto> all = userDetailsClient.getAllUserDetails(page, size);
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("/user/by_username")
    ResponseEntity<?> getUserByUsername(@RequestParam String username) {
        return new ResponseEntity<>(userDetailsClient.getUserDetailsByUsername(username), HttpStatus.OK);
    }

    @PostMapping("/article")
    ResponseEntity<?> saveNewArticle(@RequestBody NewArticleReqDto dto, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        dto.setAuthor(userId);
        try {
            ArticleRespDto saved = articleClient.saveArticle(dto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (BadRequestException e){
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/article/{id}")
    ResponseEntity<?> updateArticle(@RequestBody ArticleRespDto dto, @PathVariable UUID id, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        dto.setAuthor(userId);
        try {
            ArticleRespDto resp = articleClient.updateArticle(dto, id);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (BadRequestException e){
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/article/{id}")
    ResponseEntity<?> deleteArticle(@PathVariable UUID id, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ArticleRespDto a = articleClient.getArticle(id);
        if(a==null) return new ResponseEntity<>(HttpStatus.OK);
        if(!a.getAuthor().equals(userId)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        articleClient.deleteArticle(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/article/search")
    ResponseEntity<?> searchArticles(
            @RequestParam(required = false) UUID author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return new ResponseEntity<>(articleClient.searchArticle(
                author, title, tags, start, end, page, size
        ), HttpStatus.OK);
    }

    @GetMapping("/topic/{id}")
    ResponseEntity<?> getTopic(@PathVariable UUID id){
        return new ResponseEntity<>(articleClient.getTopic(id), HttpStatus.OK);
    }

    @PostMapping("/topic")
    ResponseEntity<?> saveTopic(@RequestBody NewTopicReqDto dto, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        dto.setAuthor(userId);
        try {
            TopicRespDto resp = articleClient.saveTopic(dto);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (BadRequestException e){
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/topic/all")
    ResponseEntity<?> getAllTopics(@RequestParam(required = false) String title){
        if (title==null) {
            return new ResponseEntity<>(articleClient.getAllTopics(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(articleClient.getAllTopics(title), HttpStatus.OK);
        }
    }

    @PutMapping("/topic/{id}")
    ResponseEntity<?> updateTopic(@PathVariable UUID id, @RequestBody NewTopicReqDto dto, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        dto.setAuthor(userId);
        try {
            TopicRespDto resp = articleClient.updateTopic(dto, id);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (BadRequestException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/topic/{id}")
    ResponseEntity<?> deleteTopic(@PathVariable UUID id, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        TopicRespDto topic = articleClient.getTopic(id);
        if(topic==null) return new ResponseEntity<>(HttpStatus.OK);
        if(!topic.getAuthor().equals(userId)){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        articleClient.deleteTopic(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/topic/all/by_user/{id}")
    ResponseEntity<?> getTopicsForUser(@PathVariable UUID id){
        return new ResponseEntity<>(articleClient.getTopics(id), HttpStatus.OK);
    }

    @GetMapping("/comment")
    ResponseEntity<?> getCommentsForArticle(@RequestParam UUID postId){
        return new ResponseEntity<>(activityClient.getComments(postId), HttpStatus.OK);
    }

    @PostMapping("/comment")
    ResponseEntity<?> createComment(@RequestBody CommentReqDto dto, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        dto.setAuthorId(userId);
        try {
            CommentRespDto resp = activityClient.saveComment(dto);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (BadRequestException e){
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/comment/{id}")
    ResponseEntity<?> updateComment(@PathVariable UUID id, @RequestBody CommentReqDto dto, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        CommentRespDto c = activityClient.getComment(id);
        if(c==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        dto.setAuthorId(c.getAuthorId());
        dto.setArticleId(c.getArticleId());
        ArticleRespDto article = articleClient.getArticle(dto.getArticleId());
        if(article==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            return new ResponseEntity<>(activityClient.updateComment(id, dto), HttpStatus.OK);
        } catch (BadRequestException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/comment/{id}")
    ResponseEntity<?> deleteComment(@PathVariable UUID id, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        CommentRespDto c = activityClient.getComment(id);
        if(c != null && !c.getAuthorId().equals(userId)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        activityClient.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/subscription/all")
    ResponseEntity<?> getAllCommentsForUser(@RequestParam(required = false) UUID userId, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID uId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (uId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        if (userId==null) {
          userId =  uId;
        }
        return new ResponseEntity<>(activityClient.getSubscriptions(userId), HttpStatus.OK);
    }

    @PostMapping("/subscription")
    ResponseEntity<?> createSubscription(@RequestBody SubscriptionReqDto dto, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID uId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (uId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        dto.setUserId(uId);
        UUID topicId = dto.getTopicId();
        if (topicId==null || articleClient.getTopic(topicId) == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            SubscriptionRespDto resp = activityClient.saveSubscription(dto);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (BadRequestException e){
            return new ResponseEntity<>(e.getBody(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/subscription/{id}")
    ResponseEntity<?> seleteSubscription(@PathVariable UUID id, HttpServletRequest req){
        String jwt = tokenProvider.resolveToken(req);
        UUID userId = userService.getDetailsUUIDBySecretUUID(jwt);
        if (userId == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        SubscriptionRespDto s = activityClient.getSubscription(id);
        if(s!=null && !s.getUserId().equals(userId)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        activityClient.deleteSubscription(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
