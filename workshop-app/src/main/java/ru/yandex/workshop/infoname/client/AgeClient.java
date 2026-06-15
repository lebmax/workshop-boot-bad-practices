package ru.yandex.workshop.infoname.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import ru.yandex.workshop.infoname.model.InfoNameService;

import java.util.Map;
import java.util.Optional;

@Repository
public class AgeClient {

    @Autowired
    private InfoNameService infoNameService;
    private final RestClient ageRestClient = RestClient.create();

    public String getAge(String name) {
        var response = ageRestClient.get()
            .uri(infoNameService.ageClientUri, name)
            .retrieve()
            .body(Map.class);
        return Optional.ofNullable(response.get("age"))
            .map(it -> it + " лет (средний)")
            .orElse("Нет данных");
    }

}
