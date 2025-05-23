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
        return switch (zona) {
            case "GMT-5" -> "America/New_York";
            case "Hora de Greenwich" -> "Europe/London";
            case "Hora de Japón" -> "Asia/Tokyo";
            case "Hora de Sídney" -> "Australia/Sydney";
            default -> throw new IllegalArgumentException("Zona horaria no soportada: " + zona);
        };
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

    public List<Map<String, Object>> getExecutedOrdersFromAlpaca() {
        String url = BASE_URL + "/orders?status=all&nested=true"; // trae todas las órdenes, incluidas ejecutadas

        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        List<Map<String, Object>> allOrders = response.getBody();

        // Filtramos y transformamos para devolver solo la info relevante
        return allOrders.stream()
                .filter(order -> order.get("filled_at") != null)
                .map(order -> {
                    String symbol = order.get("symbol") != null ? order.get("symbol").toString() : "N/A";

                    return Map.of(
                            "symbol", symbol,
                            "order_type", order.get("type"),
                            "side", order.get("side"),
                            "qty", order.get("qty"),
                            "filled_qty", order.get("filled_qty"),
                            "avg_fill_price", order.get("filled_avg_price"),
                            "filled_at", order.get("filled_at"),
                            "valor_total", calculateValorTotal(order)
                    );
                })
                .toList();
    }

    // Método auxiliar para calcular el valor total
    private double calculateValorTotal(Map<String, Object> order) {
        try {
            double qty = Double.parseDouble(order.get("filled_qty").toString());
            double price = Double.parseDouble(order.get("filled_avg_price").toString());
            return qty * price;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public List<Map<String, Object>> getResumenPortafolio() {
        String url = BASE_URL + "/positions";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<Map<String, Object>> resultado = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            for (JsonNode node : root) {
                Map<String, Object> resumen = new HashMap<>();

                resumen.put("accion", node.get("symbol").asText());
                resumen.put("cantidad", node.get("qty").asInt());
                resumen.put("precio_promedio", node.get("avg_entry_price").asDouble());
                resumen.put("valor_actual", node.get("market_value").asDouble());
                resumen.put("porcentaje_ganancia", node.get("unrealized_plpc").asDouble() * 100); // % ganancia

                resultado.add(resumen);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public List<Map<String, Object>> getDistribucionPortafolio() {
        String url = BASE_URL + "/positions";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<Map<String, Object>> resultado = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            double valorTotal = 0.0;
            Map<String, Double> valoresPorAccion = new HashMap<>();

            // 1. Sumar valor total y agrupar por símbolo
            for (JsonNode node : root) {
                String symbol = node.get("symbol").asText();
                double marketValue = node.get("market_value").asDouble();

                valoresPorAccion.put(symbol, marketValue);
                valorTotal += marketValue;
            }

            // 2. Calcular porcentaje por acción
            for (Map.Entry<String, Double> entry : valoresPorAccion.entrySet()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("accion", entry.getKey());
                fila.put("porcentaje", Math.round((entry.getValue() / valorTotal) * 10000.0) / 100.0); // redondear a 2 decimales
                resultado.add(fila);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public List<Map<String, Object>> getConteoOrdenesPorTipo() {
        String url = BASE_URL + "/orders?status=all";
        HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        List<Map<String, Object>> resultado = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            Map<String, Integer> conteoPorTipo = new HashMap<>();

            for (JsonNode order : root) {
                String tipo = order.get("type").asText();
                conteoPorTipo.put(tipo, conteoPorTipo.getOrDefault(tipo, 0) + 1);
            }

            for (Map.Entry<String, Integer> entry : conteoPorTipo.entrySet()) {
                Map<String, Object> tipoOrden = new HashMap<>();
                tipoOrden.put("tipo", entry.getKey());
                tipoOrden.put("cantidad", entry.getValue());
                resultado.add(tipoOrden);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    public Map<String, Object> getPortfolioSnapshot() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. Obtener balance
            String balanceJson = getAccountBalance();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(balanceJson);

            double equity = node.path("equity").asDouble();
            double cash = node.path("cash").asDouble();
            double buyingPower = node.path("buying_power").asDouble();

            result.put("equity", equity);
            result.put("cash", cash);
            result.put("buying_power", buyingPower);

            // 2. Obtener datos de AAPL como referencia de comportamiento diario (puedes cambiar por portafolio real si usas PnL)
            String today = java.time.LocalDate.now().toString();
            List<Map<String, Object>> candles = getHistoricalCandles("AAPL", "1D", today + "T00:00:00Z", today + "T23:59:59Z");

            result.put("historical_equity", candles);

            // 3. Cambio diario aproximado (solo si hay al menos 2 puntos)
            if (candles.size() > 1) {
                double open = (double) candles.get(0).get("open");
                double close = (double) candles.get(candles.size() - 1).get("close");
                result.put("daily_change", close - open);
            } else {
                result.put("daily_change", 0);
            }
        } catch (Exception e) {
            result.put("error", "Error al generar el snapshot: " + e.getMessage());
        }
        return result;
    }

}

