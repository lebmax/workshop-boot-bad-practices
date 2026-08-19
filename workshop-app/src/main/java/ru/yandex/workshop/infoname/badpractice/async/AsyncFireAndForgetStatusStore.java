package ru.yandex.workshop.infoname.badpractice.async;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AsyncFireAndForgetStatusStore {

    private static final String PENDING = "PENDING";
    private static final String FAILED = "FAILED";
    private static final String UNKNOWN = "UNKNOWN";

    private final ConcurrentHashMap<String, String> statuses = new ConcurrentHashMap<>();

    public void markPending(String marker) {
        statuses.put(marker, PENDING);
    }

    public void markFailed(String marker) {
        statuses.put(marker, FAILED);
    }

    public AsyncFireAndForgetStatus getStatus(String marker) {
        return new AsyncFireAndForgetStatus(marker, statuses.getOrDefault(marker, UNKNOWN));
    }
}
