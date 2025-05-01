package com.edu.unbosque.service;

import com.edu.unbosque.model.Orden;
import com.edu.unbosque.repository.OrdenRepository;
import org.springframework.http.*;

import com.edu.unbosque.model.Accion;
import com.edu.unbosque.model.Notificacion;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.repository.AccionRepository;
import com.edu.unbosque.repository.NotificacionRepository;
import com.edu.unbosque.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


@Service
public class AlpacaService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AccionRepository accionRepository;

    @Autowired
    private OrdenRepository ordenRepository;

    @Value("${alpaca.api.key}")
    private String apiKey;

    @Value("${alpaca.api.secret}")
    private String apiSecret;

    private final String BASE_URL = "https://paper-api.alpaca.markets/v2"; // Endpoint para el entorno de pruebas

    private final String MARKET_DATA_URL = "https://data.alpaca.markets/v2";
    // RestTemplate para hacer las peticiones HTTP
    private final RestTemplate restTemplate;

    public AlpacaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Obtener el balance de la cuenta
    public String getAccountBalance() {
        String url = BASE_URL + "/account";
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();  // Devuelve el balance en formato JSON
    }

    // Obtener cotización de una acción
    public Map<String, Object> getStockQuote(String symbol) {
        String url = MARKET_DATA_URL + "/stocks/" + symbol + "/quotes/latest";

        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
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

    // Realizar una orden de compra
    public String placeBuyOrder(String symbol, int qty, double price) {
        String url = BASE_URL + "/orders";

        String body = "{"
                + "\"symbol\":\"" + symbol + "\","
                + "\"qty\":" + qty + ","
                + "\"side\":\"buy\","
                + "\"type\":\"limit\","
                + "\"time_in_force\":\"gtc\","
                + "\"limit_price\":" + price
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON); // <-- ESTA LÍNEA ES CLAVE

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    public String getOpenPositions() {
        String url = BASE_URL + "/positions";
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);
        return headers;
    }


    public String getAvailableAssets() {
        String url = BASE_URL + "/assets?status=active&class=us_equity";
        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }


    public boolean crearAlerta(int id_usuario, int id_orden, String tipoAlerta, double valorObjetivo, String canal) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id_usuario);
            Optional<Orden> ordenOpt = ordenRepository.findById(id_orden);

            if (usuarioOpt.isEmpty()) {
                System.out.println("❌ Usuario no encontrado con ID: " + id_usuario);
                return false;
            }

            if (ordenOpt.isEmpty()) {
                System.out.println("❌ Acción no encontrada con ID: " + id_orden);
                return false;
            }

            Notificacion notificacion = new Notificacion();
            notificacion.setTipoAlerta(tipoAlerta);
            notificacion.setValorObjetivo(valorObjetivo);
            notificacion.setCanal(canal);
            notificacion.setEstado(true);
            notificacion.setUsuario(usuarioOpt.get());
            notificacion.setOrden(ordenOpt.get());

            System.out.println("✅ Guardando notificación: " + notificacion);

            notificacionRepository.save(notificacion);
            return true;

        } catch (Exception e) {
            System.out.println("❌ ERROR AL CREAR ALERTA");
            e.printStackTrace(); // Esto imprime el error real
            throw new RuntimeException("Error al crear la alerta", e); // Para ver el 500 en detalle
        }
    }

    // Obtener datos históricos de precios
    public List<Map<String, Object>> getHistoricalCandles(String symbol, String timeframe, String start, String end) {
        String url = MARKET_DATA_URL + "/stocks/" + symbol + "/bars?timeframe=" + timeframe + "&start=" + start + "&end=" + end;

        HttpHeaders headers = buildHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

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

    // 1. MARKET ORDER
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

    // 2. STOP LOSS ORDER
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

    // 3. TAKE PROFIT ORDER
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

            // Verifica si existe el precio promedio de la orden
            if (root.has("filled_avg_price") && !root.get("filled_avg_price").isNull()) {
                return root.get("filled_avg_price").asDouble();
            }

            // Si la respuesta contiene un mensaje de error relacionado con el mercado cerrado, puede devolver un código específico
            if (root.has("error")) {
                String errorMessage = root.get("error").asText();
                if (errorMessage.contains("market closed")) {
                    return -2.0; // Usamos un código especial para indicar que el mercado está cerrado
                }
            }

            // Si no existe o es null, devuelve un valor especial para indicar que no se obtuvo el precio
            return -1.0; // Indicando que no se obtuvo el precio

        } catch (Exception e) {
            e.printStackTrace();
            return -1.0; // Si hay un error en el procesamiento, devuelve -1.0
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

            return root.path("quote").path("ap").asDouble(); // Puedes cambiarlo por "bp" o "p" si prefieres otro valor

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







}