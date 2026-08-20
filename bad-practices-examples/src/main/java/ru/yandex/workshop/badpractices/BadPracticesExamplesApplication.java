package ru.yandex.workshop.badpractices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BadPracticesExamplesApplication {

    public static void main(String[] args) {
        SpringApplication.run(BadPracticesExamplesApplication.class, args);
    }
}
