package com.example.pzapigateway.exception;

import lombok.Getter;

@Getter
public class ForbiddenRequestException extends RuntimeException{
    private final String body;

    public ForbiddenRequestException(String body) {
        super();
        this.body = body;
    }
}
