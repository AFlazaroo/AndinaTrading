package com.edu.unbosque.controller;

import com.edu.unbosque.service.AlpacaApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
public class AlpacaController {

    private final AlpacaApiClient alpacaApiClient;

    @Autowired
    public AlpacaController(AlpacaApiClient alpacaApiClient) {
        this.alpacaApiClient = alpacaApiClient;
    }

    @GetMapping("/api/alpaca/account")
    public ResponseEntity<String> getAccountInfo() {
        return alpacaApiClient.getAccountInfo();
    }
}