package com.cookmate.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CookmateBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookmateBackendApplication.class, args);
    }
}