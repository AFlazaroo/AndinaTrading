package co.edu.unbosque.microservice.microserviceibkradapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketDataDTO {
    private String symbol;
    private Double lastPrice;
    private String status;
}