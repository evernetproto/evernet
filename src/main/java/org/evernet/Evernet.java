package org.evernet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Evernet {
    public static void main(String[] args) {
        SpringApplication.run(Evernet.class, args);
    }
}