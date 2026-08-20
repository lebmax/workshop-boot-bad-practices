package ru.yandex.workshop.badpractices.concurrency;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class LostUpdateService {

    private static final int COUNTER_ID = 1;
    private static final int MIN_PARALLEL_REQUESTS = 2;
    private static final int MAX_PARALLEL_REQUESTS = 4;
    private static final long WORKER_TIMEOUT_SECONDS = 15;
    private static final long EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final JdbcTemplate jdbcTemplate;
    private final LostUpdateWorker lostUpdateWorker;

    public LostUpdateService(JdbcTemplate jdbcTemplate, LostUpdateWorker lostUpdateWorker) {
        this.jdbcTemplate = jdbcTemplate;
        this.lostUpdateWorker = lostUpdateWorker;
    }

    public LostUpdateResult run(int parallelRequests) {
        validateParallelRequests(parallelRequests);
        resetCounter();

        ExecutorService executor = Executors.newFixedThreadPool(parallelRequests);
        try {
            CountDownLatch allWorkersRead = new CountDownLatch(parallelRequests);
            List<Callable<Void>> tasks = createTasks(parallelRequests, allWorkersRead);
            List<Future<Void>> futures = executor.invokeAll(
                tasks,
                WORKER_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
            );
            executor.shutdown();
            ensureWorkersCompleted(futures);

            long actual = readCounter();
            return new LostUpdateResult(parallelRequests, actual, (long) parallelRequests - actual);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LostUpdateExecutionException("Lost-update execution was interrupted", e);
        } catch (ExecutionException | CancellationException e) {
            throw new LostUpdateExecutionException("Lost-update worker failed", causeOf(e));
        } finally {
            shutdownExecutor(executor);
        }
    }

    private List<Callable<Void>> createTasks(
        int parallelRequests,
        CountDownLatch allWorkersRead
    ) {
        List<Callable<Void>> tasks = new ArrayList<>(parallelRequests);
        for (int i = 0; i < parallelRequests; i++) {
            tasks.add(() -> {
                lostUpdateWorker.readAndIncrement(allWorkersRead);
                return null;
            });
        }
        return tasks;
    }

    private static void ensureWorkersCompleted(List<Future<Void>> futures)
        throws ExecutionException, InterruptedException {
        for (Future<Void> future : futures) {
            if (future.isCancelled()) {
                throw new CancellationException("Lost-update worker timed out");
            }
            future.get();
        }
    }

    private void resetCounter() {
        int updatedRows = jdbcTemplate.update(
            "UPDATE lost_update_counter SET counter_value = ? WHERE id = ?",
            0L,
            COUNTER_ID
        );
        if (updatedRows != 1) {
            throw new IllegalStateException("Counter row with id=1 was not reset");
        }
    }

    private long readCounter() {
        Long actual = jdbcTemplate.queryForObject(
            "SELECT counter_value FROM lost_update_counter WHERE id = ?",
            Long.class,
            COUNTER_ID
        );
        if (actual == null) {
            throw new IllegalStateException("Counter row with id=1 was not found");
        }
        return actual;
    }

    private static void validateParallelRequests(int parallelRequests) {
        if (parallelRequests < MIN_PARALLEL_REQUESTS
            || parallelRequests > MAX_PARALLEL_REQUESTS) {
            throw new IllegalArgumentException(
                "parallelRequests must be between %d and %d"
                    .formatted(MIN_PARALLEL_REQUESTS, MAX_PARALLEL_REQUESTS)
            );
        }
    }

    private static Throwable causeOf(Exception exception) {
        Throwable cause = exception.getCause();
        return cause == null ? exception : cause;
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(
                EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
            )) {
                executor.shutdownNow();
                if (!executor.awaitTermination(
                    EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
                )) {
                    throw new LostUpdateExecutionException(
                        "Lost-update executor did not terminate"
                    );
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static final class LostUpdateExecutionException extends RuntimeException {

        private LostUpdateExecutionException(String message) {
            super(message);
        }

        private LostUpdateExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
