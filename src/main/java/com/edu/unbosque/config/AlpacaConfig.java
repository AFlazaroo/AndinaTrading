package com.edu.unbosque.config;

import com.edu.unbosque.service.AlpacaApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlpacaConfig {
    @Value("${alpaca.api.key}")
    private String apiKey;

    @Value("${alpaca.secret.key}")
    private String secretKey;

    @Bean
    public AlpacaApiClient alpacaApiClient() {
        return new AlpacaApiClient(apiKey, secretKey);
    }
}
