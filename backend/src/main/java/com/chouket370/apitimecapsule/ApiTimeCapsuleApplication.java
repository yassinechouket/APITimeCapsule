package com.chouket370.apitimecapsule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiTimeCapsuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiTimeCapsuleApplication.class, args);
    }

}
