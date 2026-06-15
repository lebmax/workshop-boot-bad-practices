package ru.yandex.workshop.ratelimiter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiter")
public record RateLimiterProperties(
	Boolean enabled,
	Integer attemptsLimit,
	Integer attemptExpirationSeconds
) {}
