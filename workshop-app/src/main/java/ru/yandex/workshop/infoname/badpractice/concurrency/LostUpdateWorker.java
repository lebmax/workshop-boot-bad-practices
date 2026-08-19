package ru.yandex.workshop.infoname.badpractice.concurrency;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class LostUpdateWorker {

    private static final int COUNTER_ID = 1;
    private static final long READ_BARRIER_TIMEOUT_SECONDS = 10;

    private final JdbcTemplate jdbcTemplate;

    public LostUpdateWorker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void readAndIncrement(CountDownLatch allWorkersRead) {
        Long current = jdbcTemplate.queryForObject(
            "SELECT counter_value FROM lost_update_counter WHERE id = ?",
            Long.class,
            COUNTER_ID
        );
        if (current == null) {
            throw new IllegalStateException("Counter row with id=1 was not found");
        }

        allWorkersRead.countDown();
        awaitAllWorkersRead(allWorkersRead);

        jdbcTemplate.update(
            "UPDATE lost_update_counter SET counter_value = ? WHERE id = ?",
            current + 1L,
            COUNTER_ID
        );
    }

    private static void awaitAllWorkersRead(CountDownLatch allWorkersRead) {
        try {
            if (!allWorkersRead.await(READ_BARRIER_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Not all workers reached the read barrier");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Lost-update worker was interrupted", e);
        }
    }
}
