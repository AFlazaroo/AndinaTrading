package com.edu.unbosque.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;

@Service
public class AlpacaService {

    @Value("${alpaca.api.key}")
    private String apiKey;

    @Value("${alpaca.api.secret}")
    private String apiSecret;

    private final String BASE_URL = "https://paper-api.alpaca.markets/v2"; // Endpoint para el entorno de pruebas

    // RestTemplate para hacer las peticiones HTTP
    private final RestTemplate restTemplate;

    public AlpacaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Obtener el balance de la cuenta
    public String getAccountBalance() {
        String url = BASE_URL + "/account";
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();  // Devuelve el balance en formato JSON
    }

    // Obtener cotización de una acción
    public String getStockQuote(String symbol) {
        String url = BASE_URL + "/stocks/" + symbol + "/quote";
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();  // Devuelve la cotización en formato JSON
    }

    // Realizar una orden de compra
    public String placeBuyOrder(String symbol, int qty, double price) {
        String url = BASE_URL + "/orders";

        String body = "{"
                + "\"symbol\":\"" + symbol + "\","
                + "\"qty\":" + qty + ","
                + "\"side\":\"buy\","
                + "\"type\":\"limit\","
                + "\"time_in_force\":\"gtc\","
                + "\"limit_price\":\"" + price + "\""
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();  // Devuelve la respuesta de la orden
    }


    public String getOpenPositions() {
        String url = BASE_URL + "/positions";
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);
        return headers;
    }


    public String getAvailableAssets() {
        String url = BASE_URL + "/assets?status=active&class=us_equity";
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

}