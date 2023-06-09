package com.example.pzapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PzApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PzApiGatewayApplication.class, args);
    }

}
