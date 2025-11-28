package com.detroitchow.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DetroitChowAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(DetroitChowAdminApplication.class, args);
    }
}
