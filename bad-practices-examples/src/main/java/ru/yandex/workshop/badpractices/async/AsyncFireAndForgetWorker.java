package ru.yandex.workshop.badpractices.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncFireAndForgetWorker {

    private static final long FAILURE_DELAY_MILLIS = 500;

    private final AsyncFireAndForgetStatusStore statusStore;

    public AsyncFireAndForgetWorker(AsyncFireAndForgetStatusStore statusStore) {
        this.statusStore = statusStore;
    }

    @Async
    public void failAsync(String marker) {
        try {
            Thread.sleep(FAILURE_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            statusStore.markFailed(marker);
            throw new IllegalStateException("Async operation was interrupted", e);
        }

        statusStore.markFailed(marker);
        throw new IllegalStateException("Intentional fire-and-forget failure: " + marker);
    }
}
