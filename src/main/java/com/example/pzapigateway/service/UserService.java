package com.example.pzapigateway.service;

import com.example.pzapigateway.exception.ForbiddenRequestException;
import com.example.pzapigateway.web.client.ArticleClient;
import com.example.pzapigateway.web.client.UserDetailsClient;
import com.example.pzapigateway.web.client.UserSecurityClient;
import com.example.pzapigateway.web.dto.secutity.UserSecretDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private UserSecurityClient securityClient;

    public UUID getDetailsUUIDBySecretUUID(String jwt){
        try {
            UserSecretDto dto = securityClient.getUserSecret("Bearer " + jwt);
            return dto.getDetailsId();
        } catch (ForbiddenRequestException e){
            return null;
        }
    }

}
