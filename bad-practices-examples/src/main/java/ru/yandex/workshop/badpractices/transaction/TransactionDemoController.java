package ru.yandex.workshop.badpractices.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transaction-demo")
public class TransactionDemoController {

    private static final long MAX_DELAY_MILLIS = 60_000;

    private final TransactionalNetworkService transactionalNetworkService;

    public TransactionDemoController(TransactionalNetworkService transactionalNetworkService) {
        this.transactionalNetworkService = transactionalNetworkService;
    }

    @GetMapping("bad")
    public String bad(@RequestParam(defaultValue = "3000") long delayMillis) {
        validateDelay(delayMillis);
        return transactionalNetworkService.saveWithSlowNetworkCall(delayMillis);
    }

    @GetMapping("slow-network")
    public String slowNetwork(@RequestParam long delayMillis) throws InterruptedException {
        validateDelay(delayMillis);
        Thread.sleep(delayMillis);
        return "Ответ получен через %d мс".formatted(delayMillis);
    }

    private static void validateDelay(long delayMillis) {
        if (delayMillis < 0 || delayMillis > MAX_DELAY_MILLIS) {
            throw new IllegalArgumentException(
                "delayMillis должен быть в диапазоне от 0 до %d".formatted(MAX_DELAY_MILLIS)
            );
        }
    }
}
