package ru.yandex.workshop.infoname.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import ru.yandex.workshop.infoname.model.InfoNameService;
import ru.yandex.workshop.infoname.util.TranslateUtil;

import java.util.Map;
import java.util.Optional;

import static ru.yandex.workshop.infoname.util.TranslateUtil.translateToProbabilityEnding;

@Repository
public class GenderClient {

    @Autowired
    private InfoNameService infoNameService;

    private final RestClient genderRestClient = RestClient.create();

    public String getGender(String name) {
        var response = genderRestClient.get()
            .uri(infoNameService.genderClientUri, name)
            .retrieve()
            .body(Map.class);
        return Optional.ofNullable(response.get("gender"))
            .map(Object::toString)
            .map(TranslateUtil::translateGender)
            .orElse("Неизвестно")
            + translateToProbabilityEnding((Double) response.get("probability"));
    }
}
