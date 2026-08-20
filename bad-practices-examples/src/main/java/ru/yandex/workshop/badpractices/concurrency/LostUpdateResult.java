package ru.yandex.workshop.badpractices.concurrency;

public record LostUpdateResult(long expected, long actual, long lostUpdates) {
}
