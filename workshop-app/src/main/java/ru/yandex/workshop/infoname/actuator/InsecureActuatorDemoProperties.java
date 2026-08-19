package ru.yandex.workshop.infoname.actuator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("insecure-actuator")
@ConfigurationProperties(prefix = "workshop.insecure-actuator-demo")
public class InsecureActuatorDemoProperties {

    private String visibleValue;

    public String getVisibleValue() {
        return visibleValue;
    }

    public void setVisibleValue(String visibleValue) {
        this.visibleValue = visibleValue;
    }
}
