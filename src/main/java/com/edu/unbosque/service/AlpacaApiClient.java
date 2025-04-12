package com.edu.unbosque.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class AlpacaApiClient {
private final String BASE_URL = "https://paper-api.alpaca.markets/v2"; // URL de la API de Alpaca
private final String apiKey;
private final String secretKey;

private final RestTemplate restTemplate;

public AlpacaApiClient(String apiKey, String secretKey) {
    this.apiKey = apiKey;
    this.secretKey = secretKey;
    this.restTemplate = new RestTemplate();
}

public ResponseEntity<String> getAccountInfo() {
    String url = BASE_URL + "/account";
    return restTemplate.getForEntity(url, String.class); // Obtiene la cuenta
}

// Puedes agregar más métodos para operaciones como órdenes, balances, etc.
}
