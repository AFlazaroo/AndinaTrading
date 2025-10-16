package co.edu.unbosque.microservice.controller;

import co.edu.unbosque.microservice.client.IBKRAdapterClient;
import co.edu.unbosque.microservice.model.MarketDataDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bolsa")
public class BolsaController {

    private final IBKRAdapterClient ibkrAdapterClient;

    @Autowired
    public BolsaController(IBKRAdapterClient ibkrAdapterClient) {
        this.ibkrAdapterClient = ibkrAdapterClient;
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<MarketDataDTO> getAssetPrice(@PathVariable String symbol) {
        // Aquí ocurre la magia: llamamos al método de la interfaz Feign...
        // ...y Feign se encarga de encontrar 'ibkr-adapter' en Eureka, construir la URL
        // y hacer la llamada HTTP por nosotros.
        return ibkrAdapterClient.getMarketData(symbol);
    }
}