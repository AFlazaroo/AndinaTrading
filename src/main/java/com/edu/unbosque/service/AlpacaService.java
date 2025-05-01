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
        String url = MARKET_DATA_URL + "/stocks/" + symbol + "/bars?timeframe=" + timeframe + "&start=" + start + "&end=" + end;
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
}
