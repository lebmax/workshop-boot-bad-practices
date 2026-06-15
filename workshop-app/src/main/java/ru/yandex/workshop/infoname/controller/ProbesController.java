package ru.yandex.workshop.infoname.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/actuator/health")
public class ProbesController {

    @GetMapping
    public ResponseEntity<Object> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @GetMapping("/liveness")
    public ResponseEntity<Object> liveness() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @GetMapping("/readiness")
    public ResponseEntity<Object> readiness() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
