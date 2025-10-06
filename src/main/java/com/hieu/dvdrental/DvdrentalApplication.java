package com.hieu.dvdrental;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class DvdrentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DvdrentalApplication.class, args);
    }

    @Bean
    public CommandLineRunner clr() {
        return args -> {

        };
    }
}
