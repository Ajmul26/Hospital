package com.example.springcrudh2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringCrudH2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringCrudH2Application.class, args);
    }
}