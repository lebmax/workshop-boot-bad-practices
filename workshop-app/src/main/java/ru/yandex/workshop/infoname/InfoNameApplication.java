package ru.yandex.workshop.infoname;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.yandex.workshop.ratelimiter.RateLimiterAutoConfiguration;

@SpringBootApplication
@SpringBootConfiguration
@EnableAsync
public class InfoNameApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfoNameApplication.class);
    }

}
