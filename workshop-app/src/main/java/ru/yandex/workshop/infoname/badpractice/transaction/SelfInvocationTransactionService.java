package ru.yandex.workshop.infoname.badpractice.transaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SelfInvocationTransactionService {

    private static final String SCENARIO = "self-invocation";
    private static final String INSERT_SQL = """
        INSERT INTO transaction_pitfall_log (scenario, marker, created_at)
        VALUES (?, ?, CURRENT_TIMESTAMP)
        """;
    private static final String EXISTS_SQL = """
        SELECT COUNT(*)
        FROM transaction_pitfall_log
        WHERE scenario = ? AND marker = ?
        """;

    private final JdbcTemplate jdbcTemplate;

    public SelfInvocationTransactionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TransactionPitfallResult demonstrate(String marker) {
        try {
            this.insertAndFail(marker);
        } catch (RuntimeException ignored) {
            // The caller intentionally handles the exception so the request can report the outcome.
        }

        boolean persisted = isPersisted(marker);
        return new TransactionPitfallResult(
            SCENARIO,
            marker,
            persisted,
            "The @Transactional method was called through this, so the proxy was bypassed; "
                + "the INSERT ran in auto-commit mode and survived the RuntimeException."
        );
    }

    @Transactional
    public void insertAndFail(String marker) {
        jdbcTemplate.update(INSERT_SQL, SCENARIO, marker);
        throw new IllegalStateException("Intentional failure after INSERT");
    }

    private boolean isPersisted(String marker) {
        Long count = jdbcTemplate.queryForObject(EXISTS_SQL, Long.class, SCENARIO, marker);
        return count != null && count > 0;
    }
}
