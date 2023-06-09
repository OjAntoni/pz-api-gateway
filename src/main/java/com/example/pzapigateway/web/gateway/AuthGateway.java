package com.example.pzapigateway.web.gateway;

import com.example.pzapigateway.exception.BadRequestException;
import com.example.pzapigateway.exception.ForbiddenRequestException;
import com.example.pzapigateway.exception.NoContentException;
import com.example.pzapigateway.security.JwtTokenProvider;
import com.example.pzapigateway.service.UserService;
import com.example.pzapigateway.web.client.UserDetailsClient;
import com.example.pzapigateway.web.client.UserSecurityClient;
import com.example.pzapigateway.web.dto.details.NewUserLocationResp;
import com.example.pzapigateway.web.dto.details.PatchUserDto;
import com.example.pzapigateway.web.dto.details.UserDto;
import com.example.pzapigateway.web.dto.details.UserRegistrationDto;
import com.example.pzapigateway.web.dto.gateway.NewUserReqDto;
import com.example.pzapigateway.web.dto.gateway.UserLoginReqDto;
import com.example.pzapigateway.web.dto.secutity.NewUserSecretReqDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<?> userById(HttpServletRequest req) {
        String jwt = tokenProvider.resolveToken(req);
        UUID id = userService.getDetailsUUIDBySecretUUID(jwt);
        if (id == null) return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        UserDto byId = userDetailsClient.getUserDetailsById(id);
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
}
