package com.example.pzapigateway.config;

import com.example.pzapigateway.exception.BadRequestException;
import com.example.pzapigateway.exception.ForbiddenRequestException;
import com.example.pzapigateway.exception.NoContentException;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    @SneakyThrows
    public Exception decode(String methodKey, Response response) {
        if(response.status()== HttpStatus.BAD_REQUEST.value()){
            throw new BadRequestException(getBody(response));
        }
        if (response.status()==HttpStatus.FORBIDDEN.value()){
            throw new ForbiddenRequestException(getBody(response));
        }
        if (response.status()==HttpStatus.NO_CONTENT.value()){
            throw new NoContentException(getBody(response));
        }
        // Return null to indicate that no error decoding is necessary
        return null;
    }

    public String getBody(Response response) throws IOException {
        if (response!=null && response.body() != null) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } else {
            return "";
        }
    }
}

