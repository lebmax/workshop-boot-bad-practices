package ru.yandex.workshop.infoname.transaction;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class TransactionalNetworkService {

    private final JdbcTemplate jdbcTemplate;
    private final RestClient restClient;
    private final String slowNetworkUrl;

    public TransactionalNetworkService(
        JdbcTemplate jdbcTemplate,
        RestClient.Builder restClientBuilder,
        @Value("${transaction-demo.slow-network-url}") String slowNetworkUrl
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.restClient = restClientBuilder.build();
        this.slowNetworkUrl = slowNetworkUrl;
    }

    @Transactional
    public String saveWithSlowNetworkCall(long delayMillis) {
        jdbcTemplate.update(
            "INSERT INTO transaction_demo_log(event_name) VALUES (?)",
            "NETWORK_CALL_STARTED"
        );

        String response = restClient.get()
            .uri(slowNetworkUrl, delayMillis)
            .retrieve()
            .body(String.class);

        jdbcTemplate.update(
            "INSERT INTO transaction_demo_log(event_name) VALUES (?)",
            "NETWORK_CALL_FINISHED"
        );
        return response;
    }
}
