package com.edu.unbosque.controller;

import com.edu.unbosque.service.AlpacaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alpaca")
public class AlpacaController {

    private final AlpacaService alpacaService;

    public AlpacaController(AlpacaService alpacaService) {
        this.alpacaService = alpacaService;
    }

    // Endpoint para obtener el balance de la cuenta
    @GetMapping("/balance")
    public String getBalance() {
        return alpacaService.getAccountBalance();
    }

    // Endpoint para obtener la cotización de una acción
    @GetMapping("/quote/{symbol}")
    public String getQuote(@PathVariable String symbol) {
        return alpacaService.getStockQuote(symbol);
    }

    // Endpoint para realizar una orden de compra
    @PostMapping("/buy")
    public String placeBuyOrder(@RequestParam String symbol, @RequestParam int qty, @RequestParam double price) {
        return alpacaService.placeBuyOrder(symbol, qty, price);
    }

    // Endpoint para obtener las posiciones abiertas (portafolio)
    @GetMapping("/positions")
    public String getOpenPositions() {
        return alpacaService.getOpenPositions();
    }


    @GetMapping("/assets")
    public String getAssets() {
        return alpacaService.getAvailableAssets();
    }

}