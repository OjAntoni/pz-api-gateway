package com.example.pzapigateway.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException{
    private final String body;

    public BadRequestException(String body) {
        super();
        this.body = body;
    }
}
