package ru.yandex.workshop.badpractices.concurrency;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LostUpdateIntegrationTest {

    @Autowired
    private LostUpdateService lostUpdateService;

    @Test
    void parallelReadModifyWriteLosesThreeUpdates() {
        LostUpdateResult result = lostUpdateService.run(4);

        assertThat(result.expected()).isEqualTo(4);
        assertThat(result.actual()).isOne();
        assertThat(result.lostUpdates()).isEqualTo(3);
    }
}
