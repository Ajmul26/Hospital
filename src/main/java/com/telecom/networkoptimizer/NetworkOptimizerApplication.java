package com.telecom.networkoptimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
@EnableScheduling
public class NetworkOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetworkOptimizerApplication.class, args);
    }
}