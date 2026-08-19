package ru.yandex.workshop.infoname.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("insecure-actuator")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InsecureActuatorProfileIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void envEndpointExposesDemoValueWithoutAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/actuator/env/workshop.insecure-actuator-demo.visible-value",
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("DEMO_ONLY_NOT_A_REAL_SECRET");
    }
}
