package ru.yandex.workshop.badpractices.transaction;

public record TransactionPitfallResult(
    String scenario,
    String marker,
    boolean persisted,
    String explanation
) {
}
