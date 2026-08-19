package ru.yandex.workshop.infoname.badpractice.transaction;

public record TransactionPitfallResult(
    String scenario,
    String marker,
    boolean persisted,
    String explanation
) {
}
