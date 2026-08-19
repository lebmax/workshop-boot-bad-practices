package ru.yandex.workshop.infoname.badpractice.concurrency;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bad-practices/concurrency")
public class LostUpdateController {

    private final LostUpdateService lostUpdateService;

    public LostUpdateController(LostUpdateService lostUpdateService) {
        this.lostUpdateService = lostUpdateService;
    }

    @PostMapping("/lost-update")
    public LostUpdateResult lostUpdate(
        @RequestParam(defaultValue = "4") int parallelRequests
    ) {
        return lostUpdateService.run(parallelRequests);
    }
}
