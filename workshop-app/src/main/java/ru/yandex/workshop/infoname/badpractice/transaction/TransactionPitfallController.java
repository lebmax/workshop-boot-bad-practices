package ru.yandex.workshop.infoname.badpractice.transaction;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bad-practices/transactions")
public class TransactionPitfallController {

    private final SelfInvocationTransactionService selfInvocationService;
    private final SwallowedExceptionTransactionService swallowedExceptionService;

    public TransactionPitfallController(
        SelfInvocationTransactionService selfInvocationService,
        SwallowedExceptionTransactionService swallowedExceptionService
    ) {
        this.selfInvocationService = selfInvocationService;
        this.swallowedExceptionService = swallowedExceptionService;
    }

    @PostMapping("/self-invocation")
    public TransactionPitfallResult selfInvocation(@RequestParam(name = "marker") String marker) {
        return selfInvocationService.demonstrate(marker);
    }

    @PostMapping("/swallowed-exception")
    public TransactionPitfallResult swallowedException(
        @RequestParam(name = "marker") String marker
    ) {
        return swallowedExceptionService.demonstrate(marker);
    }
}
