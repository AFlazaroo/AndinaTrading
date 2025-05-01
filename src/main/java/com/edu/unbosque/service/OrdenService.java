package com.edu.unbosque.service;

import com.edu.unbosque.repository.AccionRepository;
import com.edu.unbosque.repository.OrdenRepository;
import com.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para gestionar órdenes de trading con la API de Alpaca
 * Maneja la creación, ejecución y consulta de órdenes de mercado, límite, stop-loss y take-profit
 */
@Service
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;  // Repositorio para persistencia de órdenes
    private UsuarioRepository usuarioRepository;  // Repositorio de usuarios
    private AccionRepository accionRepository;  // Repositorio de acciones

    private final RestTemplate restTemplate;  // Cliente para llamadas HTTP a la API de Alpaca

    @Value("${alpaca.api.key}")
    private String apiKey;  // API key para autenticación con Alpaca

    @Value("${alpaca.api.secret}")
    private String apiSecret;  // API secret para autenticación con Alpaca

    private final String BASE_URL = "https://paper-api.alpaca.markets/v2";  // URL base de la API de Alpaca (entorno paper trading)

    public OrdenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Construye los headers HTTP necesarios para autenticación con la API de Alpaca
     * @return Objeto HttpHeaders configurado con las credenciales
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Ejecuta una orden de mercado (compra/venta inmediata al precio actual)
     * @param symbol Símbolo del activo (ej: "AAPL")
     * @param qty Cantidad de acciones a operar
     * @param side Dirección de la operación ("buy" para compra, "sell" para venta)
     * @return Respuesta de la API de Alpaca en formato JSON
     */
    public String placeMarketOrder(String symbol, int qty, String side) {
        String url = BASE_URL + "/orders";
        String body = "{" +
                "\"symbol\":\"" + symbol + "\"," +
                "\"qty\":" + qty + "," +
                "\"side\":\"" + side + "\"," +
                "\"type\":\"market\"," +
                "\"time_in_force\":\"gtc\"" +  // Good-Til-Cancelled
                "}";

        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    /**
     * Coloca una orden límite (se ejecuta solo al precio especificado o mejor)
     * @param symbol Símbolo del activo
     * @param qty Cantidad de acciones
     * @param price Precio límite para la ejecución
     * @return Respuesta de la API de Alpaca
     */
    public String placeLimitOrder(String symbol, int qty, double price) {
        String url = BASE_URL + "/orders";
        String body = "{" +
                "\"symbol\":\"" + symbol + "\"," +
                "\"qty\":" + qty + "," +
                "\"side\":\"buy\"," +  // Nota: Hardcodeado como "buy", considerar parametrizar
                "\"type\":\"limit\"," +
                "\"time_in_force\":\"gtc\"," +
                "\"limit_price\":" + price +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    /**
     * Coloca una orden stop-loss (se convierte en orden de mercado cuando se alcanza el precio stop)
     * @param symbol Símbolo del activo
     * @param qty Cantidad de acciones
     * @param side Dirección de la operación
     * @param stopPrice Precio de activación del stop
     * @return Respuesta de la API de Alpaca
     */
    public String placeStopLossOrder(String symbol, int qty, String side, double stopPrice) {
        String url = BASE_URL + "/orders";
        String body = "{" +
                "\"symbol\":\"" + symbol + "\"," +
                "\"qty\":" + qty + "," +
                "\"side\":\"" + side + "\"," +
                "\"type\":\"stop\"," +
                "\"time_in_force\":\"gtc\"," +
                "\"stop_price\":" + stopPrice +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    /**
     * Coloca una orden take-profit (orden límite para asegurar ganancias)
     * @param symbol Símbolo del activo
     * @param qty Cantidad de acciones
     * @param side Dirección de la operación
     * @param limitPrice Precio objetivo para tomar ganancias
     * @return Respuesta de la API de Alpaca
     */
    public String placeTakeProfitOrder(String symbol, int qty, String side, double limitPrice) {
        String url = BASE_URL + "/orders";
        String body = "{" +
                "\"symbol\":\"" + symbol + "\"," +
                "\"qty\":" + qty + "," +
                "\"side\":\"" + side + "\"," +
                "\"type\":\"limit\"," +
                "\"time_in_force\":\"gtc\"," +
                "\"limit_price\":" + limitPrice +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(body, buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    /**
     * Consulta el estado de una orden existente en Alpaca
     * @param orderId ID de la orden a consultar
     * @return Detalles de la orden en formato JSON
     */
    public String getOrderById(String orderId) {
        String url = BASE_URL + "/orders/" + orderId;
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
    }
}