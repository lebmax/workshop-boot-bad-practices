package ru.yandex.workshop.infoname.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.yandex.workshop.infoname.client.GenderClient;
import ru.yandex.workshop.infoname.service.InfoName;

@Component
public class InfoNameService {

    public final String genderClientUri = "https://api.genderize.io?name={name}";
    public final String ageClientUri = "https://api.agify.io?name={name}";

    @Autowired
    @Lazy
    private GenderClient genderClient;

    public InfoName getInfo(String name, String age) {
        return new InfoName(name, getGender(name), age);
    }

    private String getGender(String name) {
        return genderClient.getGender(name);
    }

}
