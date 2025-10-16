package co.edu.unbosque.microservice.client;

import co.edu.unbosque.microservice.model.MarketDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El 'name' debe ser EXACTAMENTE el "spring.application.name" del servicio que queremos llamar
@FeignClient(name = "ibkr-adapter")
public interface IBKRAdapterClient {

    @GetMapping("/api/v1/marketdata/{symbol}")
    ResponseEntity<MarketDataDTO> getMarketData(@PathVariable("symbol") String symbol);

}