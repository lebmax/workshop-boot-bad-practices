package ru.yandex.workshop.infoname.badpractice.async;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AsyncFireAndForgetIntegrationTest {

    @Autowired
    private AsyncFireAndForgetWorker asyncWorker;

    @Autowired
    private AsyncFireAndForgetStatusStore statusStore;

    @Test
    void voidAsyncFailureDoesNotReachCaller() throws InterruptedException {
        String marker = "async-test";
        statusStore.markPending(marker);

        asyncWorker.failAsync(marker);

        assertThat(statusStore.getStatus(marker).status()).isEqualTo("PENDING");
        awaitStatus(marker, "FAILED", Duration.ofSeconds(3));
    }

    private void awaitStatus(String marker, String expectedStatus, Duration timeout)
        throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (expectedStatus.equals(statusStore.getStatus(marker).status())) {
                return;
            }
            Thread.sleep(20);
        }
        assertThat(statusStore.getStatus(marker).status()).isEqualTo(expectedStatus);
    }
}
