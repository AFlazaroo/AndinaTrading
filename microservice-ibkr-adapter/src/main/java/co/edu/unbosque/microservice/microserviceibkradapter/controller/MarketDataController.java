package co.edu.unbosque.microservice.microserviceibkradapter.controller;

import co.edu.unbosque.microservice.ibkradapter.model.MarketDataDTO;
import co.edu.unbosque.microservice.ibkradapter.service.IbkrConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1/marketdata")
public class MarketDataController {

    private final IbkrConnectionService ibkrConnectionService;
    // Usamos un ID de ticker atómico para evitar colisiones en un entorno concurrente
    private final AtomicInteger tickerIdGenerator = new AtomicInteger(1000);

    @Autowired
    public MarketDataController(IbkrConnectionService ibkrConnectionService) {
        this.ibkrConnectionService = ibkrConnectionService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<MarketDataDTO> getMarketData(@PathVariable String symbol) {
        final int tickerId = tickerIdGenerator.getAndIncrement();

        ibkrConnectionService.requestMarketData(tickerId, symbol.toUpperCase());

        // --- Manejo de Asincronía (Versión Sandbox) ---
        long startTime = System.currentTimeMillis();
        long timeout = 5000; // 5 segundos de espera máxima

        Double price = null;
        while ((System.currentTimeMillis() - startTime) < timeout) {
            price = ibkrConnectionService.getMarketDataStore().get(tickerId);
            if (price != null) {
                break; // Precio recibido, salimos del bucle
            }
            try {
                Thread.sleep(100); // Espera corta para no saturar la CPU
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return ResponseEntity.status(500).body(new MarketDataDTO(symbol, null, "Request interrupted"));
            }
        }

        // Limpiamos la suscripción para no seguir recibiendo datos innecesarios
        ibkrConnectionService.cancelMarketData(tickerId);

        if (price != null) {
            return ResponseEntity.ok(new MarketDataDTO(symbol, price, "Success"));
        } else {
            return ResponseEntity.status(408).body(new MarketDataDTO(symbol, null, "Request timed out. Could not retrieve market data."));
        }
    }
}