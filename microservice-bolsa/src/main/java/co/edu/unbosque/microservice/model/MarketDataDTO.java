package co.edu.unbosque.microservice.model;

// Nota: Idealmente, esta clase DTO viviría en un módulo común compartido.
// Por ahora, para la prueba de concepto, está bien duplicarla aquí.

import lombok.Data;

@Data // Lombok genera getters, setters, toString, etc.
public class MarketDataDTO {
    private String symbol;
    private Double lastPrice;
    private String status;
}