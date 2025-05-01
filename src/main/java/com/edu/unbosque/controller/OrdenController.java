package com.edu.unbosque.controller;

import com.edu.unbosque.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrdenController {

    private final OrdenService ordenService;

    @Autowired
    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    // Orden de mercado: Ejecuta inmediatamente al precio actual del mercado
    @PostMapping("/market")
    public ResponseEntity<String> placeMarketOrder(
            @RequestParam String symbol,
            @RequestParam int qty,
            @RequestParam String side
    ) {
        try {
            String result = ordenService.placeMarketOrder(symbol, qty, side);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ejecutar orden de mercado: " + e.getMessage());
        }
    }

    // Orden límite: Se ejecuta solo cuando el activo alcanza un precio específico o mejor
    @PostMapping("/limit")
    public ResponseEntity<String> placeLimitOrder(
            @RequestParam String symbol,
            @RequestParam int qty,
            @RequestParam double price
    ) {
        try {
            String result = ordenService.placeLimitOrder(symbol, qty, price);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ejecutar orden límite: " + e.getMessage());
        }
    }

    // Orden stop-loss: Se convierte en orden de mercado cuando el precio cae a un nivel específico (protege contra pérdidas)
    @PostMapping("/stoploss")
    public ResponseEntity<String> placeStopLossOrder(
            @RequestParam String symbol,
            @RequestParam int qty,
            @RequestParam String side,
            @RequestParam double stopPrice
    ) {
        try {
            String result = ordenService.placeStopLossOrder(symbol, qty, side, stopPrice);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ejecutar orden Stop Loss: " + e.getMessage());
        }
    }

    // Orden take-profit: Se convierte en orden límite cuando el precio alcanza un nivel de beneficio objetivo
    @PostMapping("/takeprofit")
    public ResponseEntity<String> placeTakeProfitOrder(
            @RequestParam String symbol,
            @RequestParam int qty,
            @RequestParam String side,
            @RequestParam double limitPrice
    ) {
        try {
            String result = ordenService.placeTakeProfitOrder(symbol, qty, side, limitPrice);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ejecutar orden Take Profit: " + e.getMessage());
        }
    }

    // Consulta el estado de una orden existente por su ID
    @GetMapping("/{orderId}")
    public ResponseEntity<String> getOrderById(@PathVariable String orderId) {
        try {
            String order = ordenService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener la orden: " + e.getMessage());
        }
    }
}