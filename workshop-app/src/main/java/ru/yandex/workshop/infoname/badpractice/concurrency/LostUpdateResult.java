package ru.yandex.workshop.infoname.badpractice.concurrency;

public record LostUpdateResult(long expected, long actual, long lostUpdates) {
}
