package ru.yandex.workshop.badpractices.transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TransactionPitfallIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SelfInvocationTransactionService selfInvocationService;

    @Autowired
    private SwallowedExceptionTransactionService swallowedExceptionService;

    @BeforeEach
    void clearLog() {
        jdbcTemplate.update("DELETE FROM transaction_pitfall_log");
    }

    @Test
    void selfInvocationLeavesAutoCommittedRowAfterRuntimeException() {
        TransactionPitfallResult result = selfInvocationService.demonstrate("self-test");

        assertThat(result.persisted()).isTrue();
        assertThat(countRows("self-invocation", "self-test")).isOne();
    }

    @Test
    void swallowedRuntimeExceptionAllowsTransactionToCommit() {
        TransactionPitfallResult result = swallowedExceptionService.demonstrate("swallowed-test");

        assertThat(result.persisted()).isTrue();
        assertThat(countRows("swallowed-exception", "swallowed-test")).isOne();
    }

    private long countRows(String scenario, String marker) {
        Long count = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM transaction_pitfall_log
                WHERE scenario = ? AND marker = ?
                """,
            Long.class,
            scenario,
            marker
        );
        return count == null ? 0L : count;
    }
}
