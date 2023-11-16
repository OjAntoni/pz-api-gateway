package com.example.pzapigateway.service;

import com.example.pzapigateway.exception.ForbiddenRequestException;

import com.example.pzapigateway.web.client.UserDetailsClient;
import com.example.pzapigateway.web.dto.secutity.UserProfileDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private UserDetailsClient userDetailsClient;

    public long getUserID(String jwt){
        try {
            UserProfileDto dto = userDetailsClient.getUserProfile("Bearer " + jwt);
            return dto.getId();
        } catch (Exception e){
            return -1;
        }
    }

}
