package ru.yandex.workshop.infoname.badpractice.transaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SwallowedExceptionTransactionService {

    private static final String SCENARIO = "swallowed-exception";
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

    public SwallowedExceptionTransactionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public TransactionPitfallResult demonstrate(String marker) {
        try {
            jdbcTemplate.update(INSERT_SQL, SCENARIO, marker);
            throw new IllegalStateException("Intentional failure after INSERT");
        } catch (RuntimeException ignored) {
            // The exception is intentionally swallowed, so the transaction can complete normally.
        }

        boolean persisted = isPersisted(marker);
        return new TransactionPitfallResult(
            SCENARIO,
            marker,
            persisted,
            "The RuntimeException was caught inside the @Transactional method; "
                + "the method returned normally and the transaction committed."
        );
    }

    private boolean isPersisted(String marker) {
        Long count = jdbcTemplate.queryForObject(EXISTS_SQL, Long.class, SCENARIO, marker);
        return count != null && count > 0;
    }
}
