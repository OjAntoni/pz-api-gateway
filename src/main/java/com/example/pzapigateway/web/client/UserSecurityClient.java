package com.example.pzapigateway.web.client;

import com.example.pzapigateway.web.dto.gateway.UserLoginReqDto;
import com.example.pzapigateway.web.dto.secutity.NewUserSecretReqDto;
import com.example.pzapigateway.web.dto.secutity.UserSecretDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-security-client", url = "${user-security-client.url}")
public interface UserSecurityClient {
    @PostMapping("/api/v1/secret/new")
    void saveUserSecret(@RequestBody NewUserSecretReqDto dto);

    @PostMapping("/api/v1/secret/login")
    String getJwtToken(@RequestBody UserLoginReqDto dto);

    @DeleteMapping("/api/v1/secret")
    String deleteUserSecret(@RequestHeader(value = "Authorization") String authHeader);

    @GetMapping("/api/v1/secret")
    UserSecretDto getUserSecret(@RequestHeader(value = "Authorization") String authHeader);
}
