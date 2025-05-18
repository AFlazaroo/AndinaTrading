package com.edu.unbosque.service;

import com.edu.unbosque.model.Notificacion;
import com.edu.unbosque.model.Orden;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.repository.NotificacionRepository;
import com.edu.unbosque.repository.OrdenRepository;
import com.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class AlpacaService {

    @Autowired private NotificacionRepository notificacionRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private OrdenRepository ordenRepository;

    @Value("${alpaca.api.key}")
    private String apiKey;

    @Value("${alpaca.api.secret}")
    private String apiSecret;

    private final String BASE_URL = "https://paper-api.alpaca.markets/v2";
    private final String MARKET_DATA_URL = "https://data.alpaca.markets/v2";

    private final RestTemplate restTemplate;

    public AlpacaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // === 1. Balance y Activos ===
    public String getAccountBalance() {
        String url = BASE_URL + "/account";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public String getAvailableAssets() {
        String url = BASE_URL + "/assets?status=active&class=us_equity";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    // === 2. Datos de Mercado ===
    public Map<String, Object> getStockQuote(String symbol) {
        String url = MARKET_DATA_URL + "/stocks/" + symbol + "/quotes/latest";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> quoteData = new HashMap<>();
        if (response.getBody() != null && response.getBody().get("quote") != null) {
            Map<String, Object> quote = (Map<String, Object>) response.getBody().get("quote");
            quoteData.put("askPrice", quote.get("ap"));
            quoteData.put("bidPrice", quote.get("bp"));
            quoteData.put("askSize", quote.get("as"));
            quoteData.put("bidSize", quote.get("bs"));
            quoteData.put("timestamp", quote.get("t"));
        }
        return quoteData;
    }

    public List<Map<String, Object>> getHistoricalCandles(String symbol, String timeframe, String start, String end) {
        String url = MARKET_DATA_URL + "/stocks/" + symbol + "/bars?timeframe=" + timeframe +
                "&start=" + start + "&end=" + end + "&feed=iex";

        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        List<Map<String, Object>> result = new ArrayList<>();
        if (response.getBody() != null && response.getBody().get("bars") != null) {
            List<Map<String, Object>> bars = (List<Map<String, Object>>) response.getBody().get("bars");

            for (Map<String, Object> bar : bars) {
                Map<String, Object> candle = new HashMap<>();
                candle.put("timestamp", bar.get("t"));
                candle.put("open", bar.get("o"));
                candle.put("high", bar.get("h"));
                candle.put("low", bar.get("l"));
                candle.put("close", bar.get("c"));
                result.add(candle);
            }
        }

        return result;
    }

    // === 3. Gestión de Portafolio ===
    public String getOpenPositions() {
        String url = BASE_URL + "/positions";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    // === 4. Gestión de Alertas ===
    public boolean crearAlerta(int id_usuario, int id_orden, String tipoAlerta, double valorObjetivo, String canal) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id_usuario);
            Optional<Orden> ordenOpt = ordenRepository.findById(id_orden);

            if (usuarioOpt.isEmpty() || ordenOpt.isEmpty()) {
                return false;
            }

            Notificacion notificacion = new Notificacion();
            notificacion.setTipoAlerta(tipoAlerta);
            notificacion.setValorObjetivo(valorObjetivo);
            notificacion.setCanal(canal);
            notificacion.setEstado(true);
            notificacion.setUsuario(usuarioOpt.get());
            notificacion.setOrden(ordenOpt.get());

            notificacionRepository.save(notificacion);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la alerta", e);
        }
    }

    // === 5. Métodos de órdenes ===
    public String placeMarketOrder(String symbol, int qty, String side) {
        String url = BASE_URL + "/orders";

        String body = "{"
                + "\"symbol\":\"" + symbol + "\","
                + "\"qty\":" + qty + ","
                + "\"side\":\"" + side + "\","
                + "\"type\":\"market\","
                + "\"time_in_force\":\"gtc\""
                + "}";

        HttpHeaders headers = buildHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    public String placeStopLossOrder(String symbol, int qty, String side, double stopPrice) {
        String url = BASE_URL + "/orders";

        String body = "{"
                + "\"symbol\":\"" + symbol + "\","
                + "\"qty\":" + qty + ","
                + "\"side\":\"" + side + "\","
                + "\"type\":\"stop\","
                + "\"time_in_force\":\"gtc\","
                + "\"stop_price\":" + stopPrice
                + "}";

        HttpHeaders headers = buildHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    public String placeTakeProfitOrder(String symbol, int qty, String side, double limitPrice) {
        String url = BASE_URL + "/orders";

        String body = "{"
                + "\"symbol\":\"" + symbol + "\","
                + "\"qty\":" + qty + ","
                + "\"side\":\"" + side + "\","
                + "\"type\":\"limit\","
                + "\"time_in_force\":\"gtc\","
                + "\"limit_price\":" + limitPrice
                + "}";

        HttpHeaders headers = buildHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    public double extractPriceFromAlpacaResponse(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            if (root.has("filled_avg_price") && !root.get("filled_avg_price").isNull()) {
                return root.get("filled_avg_price").asDouble();
            }

            if (root.has("error")) {
                String errorMessage = root.get("error").asText();
                if (errorMessage.contains("market closed")) {
                    return -2.0;
                }
            }

            return -1.0;

        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }

    public double getPrecioActualDesdeAlpaca(String symbol) {
        String url = MARKET_DATA_URL + "/stocks/" + symbol + "/quotes/latest";
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            return root.path("quote").path("ap").asDouble();

        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }

    public String convertirZonaAHorariaJava(String zona) {
        if (zona.equals("Hora del Este de EE. UU.")) return "America/New_York";
        if (zona.equals("Hora de Greenwich")) return "Europe/London";
        if (zona.equals("Hora de Japón")) return "Asia/Tokyo";
        if (zona.equals("Hora de Sídney")) return "Australia/Sydney";
        throw new IllegalArgumentException("Zona horaria no soportada: " + zona);
    }

    public List<Map<String, Object>> getOrdenesEjecutadas() {
        String url = BASE_URL + "/orders?status=filled";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            for (JsonNode node : root) {
                Map<String, Object> orden = new HashMap<>();
                orden.put("accion", node.get("symbol").asText());
                orden.put("tipo", node.get("type").asText());
                orden.put("fecha", node.get("filled_at").asText());
                result.add(orden);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Map<String, Object>> getOrdenesPendientes() {
        String url = BASE_URL + "/orders?status=open";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            for (JsonNode node : root) {
                Map<String, Object> orden = new HashMap<>();
                orden.put("accion", node.get("symbol").asText());
                orden.put("cantidad", node.get("qty").asInt());
                orden.put("precio", node.get("limit_price") != null && !node.get("limit_price").isNull()
                        ? node.get("limit_price").asDouble()
                        : null);
                orden.put("valor", node.get("notional") != null && !node.get("notional").isNull()
                        ? node.get("notional").asDouble()
                        : null);
                orden.put("estado", node.get("status").asText());
                orden.put("tipo", node.get("type").asText());
                result.add(orden);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }




}
