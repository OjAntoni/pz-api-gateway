package com.example.pzapigateway.web.client;

import com.example.pzapigateway.web.dto.details.NewUserLocationResp;
import com.example.pzapigateway.web.dto.details.PatchUserDto;
import com.example.pzapigateway.web.dto.details.UserDto;
import com.example.pzapigateway.web.dto.details.UserRegistrationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-details-client", url = "${user-details-client.url}")
public interface UserDetailsClient {
    @PostMapping("/api/v1/user")
    NewUserLocationResp saveUserDetails(@RequestBody UserRegistrationDto dto);

    @DeleteMapping("/api/v1/user/{id}")
    void deleteUserDetails(@PathVariable UUID id);

    @GetMapping("/api/v1/user/{id}")
    UserDto getUserDetailsById(@PathVariable UUID id);

    @PutMapping("/api/v1/user/{id}")
    void updateUserDetailsById(@PathVariable UUID id, @RequestBody PatchUserDto dto);

    @DeleteMapping("/api/v1/user/{id}")
    void deleteUserDetailsById(@PathVariable UUID id);

    @GetMapping("/api/v1/user/all")
    List<UserDto> getAllUserDetails(@RequestParam int page, @RequestParam int size);

    @GetMapping("/api/v1/user/by_username")
    UserDto getUserDetailsByUsername(@RequestParam String username);
}
