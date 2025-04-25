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

import java.util.Optional;


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
    public String getStockQuote(String symbol) {
        String url = BASE_URL + "/stocks/" + symbol + "/quote";
        HttpHeaders headers = new HttpHeaders();
        headers.set("APCA-API-KEY-ID", apiKey);
        headers.set("APCA-API-SECRET-KEY", apiSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();  // Devuelve la cotización en formato JSON
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


}