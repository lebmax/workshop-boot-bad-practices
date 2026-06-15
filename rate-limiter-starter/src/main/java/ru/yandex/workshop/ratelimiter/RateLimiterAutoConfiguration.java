package ru.yandex.workshop.ratelimiter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.yandex.workshop.ratelimiter.core.RateLimiter;
import ru.yandex.workshop.ratelimiter.core.RateLimiterAspect;
import ru.yandex.workshop.ratelimiter.core.RateLimiterInMemoryImpl;

@AutoConfiguration
@EnableConfigurationProperties(RateLimiterProperties.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(
        value = "rate-limiter.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class RateLimiterAutoConfiguration {

    @Bean
    public RateLimiterAspect rateLimiterAspect(RateLimiter rateLimiter, HttpServletRequest httpServletRequest) {
        return new RateLimiterAspect(rateLimiter, httpServletRequest);
    }


    @Bean
    public RateLimiter rateLimiter(RateLimiterProperties rateLimiterProperties) {
        return new RateLimiterInMemoryImpl(rateLimiterProperties);
    }

}
