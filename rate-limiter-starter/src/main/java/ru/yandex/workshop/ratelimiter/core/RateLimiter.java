package ru.yandex.workshop.ratelimiter.core;

import ru.yandex.workshop.ratelimiter.exceptions.RateLimitExceededException;

public interface RateLimiter {

    void increment(String remoteAddress) throws RateLimitExceededException;

}
