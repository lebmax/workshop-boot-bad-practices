package ru.yandex.workshop.infoname.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InfoName(
    @JsonProperty("Имя")
    String name,
    @JsonProperty("Пол")
    String genderDescription,
    @JsonProperty("Возраст")
    String ageDescription
) {
}
