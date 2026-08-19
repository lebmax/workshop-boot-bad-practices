package ru.yandex.workshop.infoname.badpractice.async;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bad-practices/async")
public class AsyncFireAndForgetController {

    private final AsyncFireAndForgetWorker asyncWorker;
    private final AsyncFireAndForgetStatusStore statusStore;

    public AsyncFireAndForgetController(
        AsyncFireAndForgetWorker asyncWorker,
        AsyncFireAndForgetStatusStore statusStore
    ) {
        this.asyncWorker = asyncWorker;
        this.statusStore = statusStore;
    }

    @PostMapping("/fire-and-forget")
    public ResponseEntity<AsyncFireAndForgetStatus> fireAndForget(
        @RequestParam String marker
    ) {
        statusStore.markPending(marker);
        asyncWorker.failAsync(marker);
        return ResponseEntity.accepted().body(statusStore.getStatus(marker));
    }

    @GetMapping("/fire-and-forget/{marker}")
    public AsyncFireAndForgetStatus status(@PathVariable String marker) {
        return statusStore.getStatus(marker);
    }
}
