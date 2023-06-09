package com.example.pzapigateway.exception;

import lombok.Getter;

@Getter
public class NoContentException extends RuntimeException{
    private final String body;

    public NoContentException(String body) {
        super();
        this.body = body;
    }
}
