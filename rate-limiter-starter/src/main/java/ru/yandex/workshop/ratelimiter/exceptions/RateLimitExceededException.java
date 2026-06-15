package ru.yandex.workshop.ratelimiter.exceptions;

public class RateLimitExceededException extends RuntimeException {

    private final long requestsCount;

    public RateLimitExceededException(long requestsCount, String requestKey) {
        super("Requests for " + requestKey + " are not allowed anymore. Current rate — " + requestsCount);
        this.requestsCount = requestsCount;
    }

    public long getRequestsCount() {
        return requestsCount;
    }
}
