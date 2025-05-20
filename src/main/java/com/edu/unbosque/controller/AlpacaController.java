package com.edu.unbosque.controller;

import com.edu.unbosque.model.*;
import com.edu.unbosque.repository.*;
import com.edu.unbosque.service.AlpacaService;
import com.edu.unbosque.service.OrdenService;
import com.edu.unbosque.service.TransaccionService;
import com.edu.unbosque.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/alpaca")
public class AlpacaController {

    private final AlpacaService alpacaService;
    private final UsuarioRepository usuarioRepository;
    private final AccionRepository accionRepository;
    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;
    private final OrdenService ordenService;
    private final OrdenRepository ordenRepository;
    private final TransaccionService transaccionService;
    private final TransaccionRepository transaccionRepository;
    private final MercadoRepository mercadoRepository;

    @Autowired
    public AlpacaController(AlpacaService alpacaService, UsuarioRepository usuarioRepository, AccionRepository accionRepository,
                            NotificacionRepository notificacionRepository, UsuarioService usuarioService, OrdenService ordenService,
                            OrdenRepository ordenRepository, TransaccionService transaccionService, TransaccionRepository transaccionRepository,
                            MercadoRepository mercadoRepository) {
        this.alpacaService = alpacaService;
        this.usuarioRepository = usuarioRepository;
        this.accionRepository = accionRepository;
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
        this.transaccionService = transaccionService;
        this.transaccionRepository = transaccionRepository;
        this.mercadoRepository = mercadoRepository;
    }

    // === 1. Balance y Activos ===
    @GetMapping("/balance")
    public String getBalance() {
        return alpacaService.getAccountBalance();
    }

    @GetMapping("/assets")
    public String getAssets() {
        return alpacaService.getAvailableAssets();
    }

    // === 2. Datos de Mercado ===
    @GetMapping("/quote/{symbol}")
    public Map<String, Object> getQuote(@PathVariable String symbol) {
        return alpacaService.getStockQuote(symbol);
    }

    @GetMapping("/historical/{symbol}/{timeFrame}")
    public ResponseEntity<?> getHistoricalData(@PathVariable String symbol, @PathVariable String timeFrame,
                                               @RequestParam String start, @RequestParam String end) {
        try {
            return ResponseEntity.ok(alpacaService.getHistoricalCandles(symbol, timeFrame, start, end));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // === 3. Gestión de Portafolio ===
    @GetMapping("/positions")
    public String getOpenPositions() {
        return alpacaService.getOpenPositions();
    }

    // === 4. Gestión de Alertas ===
    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(@RequestParam int id_usuario, @RequestParam int id_orden,
                                              @RequestParam String tipoAlerta, @RequestParam double valorObjetivo,
                                              @RequestParam String canal) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id_usuario);
            if (usuarioOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");

            Optional<Orden> ordenOpt = ordenRepository.findById(id_orden);
            if (ordenOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no encontrada.");

            Notificacion notificacion = new Notificacion();
            notificacion.setTipoAlerta(tipoAlerta);
            notificacion.setValorObjetivo(valorObjetivo);
            notificacion.setCanal(canal);
            notificacion.setEstado(true);
            notificacion.setUsuario(usuarioOpt.get());
            notificacion.setOrden(ordenOpt.get());

            notificacionRepository.save(notificacion);
            return ResponseEntity.ok("Alerta creada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }

    // Endpoint para orden tipo MARKET
    @PostMapping("/order/market")
    public ResponseEntity<?> placeMarketOrder(@RequestParam String symbol, @RequestParam int qty,
                                              @RequestParam String side, @RequestParam Integer idUsuario) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            Accion accion = accionRepository.findByTicket(symbol)
                    .orElseThrow(() -> new RuntimeException("Acción no encontrada con ticket: " + symbol));

            Mercado mercado = accion.getMercado();
            double precioActual = alpacaService.getPrecioActualDesdeAlpaca(symbol);
            if (precioActual <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo obtener el precio actual del símbolo.");
            }

            accion.setPrecioActual(precioActual);
            accionRepository.save(accion);

            ZoneId zonaMercado = ZoneId.of(alpacaService.convertirZonaAHorariaJava(mercado.getZona_horario()));
            ZonedDateTime ahoraEnMercado = ZonedDateTime.now(zonaMercado);
            LocalTime horaActual = ahoraEnMercado.toLocalTime();

            boolean dentroHorario = horaActual.isAfter(LocalTime.from(mercado.getHorario_apertura())) &&
                    horaActual.isBefore(LocalTime.from(mercado.getHorario_cierre()));

            Orden orden = new Orden();
            orden.setAccion(accion);
            orden.setUsuario(usuarioOpt.get());
            orden.setCantidad(qty);
            orden.setPrecio(precioActual);
            orden.setTipo_orden("MARKET");
            orden.setFecha_creacion(LocalDateTime.now());
            orden.setUltima_modificacion(LocalDateTime.now());

            if (dentroHorario) {
                orden.setEstado("EJECUTADA");
                orden.setFecha_ejecucion(LocalDateTime.now());
            } else {
                orden.setEstado("PENDIENTE");
                orden.setFecha_ejecucion(null);
            }

            ordenRepository.save(orden);

            if (dentroHorario) {
                Transaccion transaccion = transaccionService.registrarTransaccion(orden);
                orden.setTransaccion(transaccion);
                ordenRepository.save(orden);
            }

            return ResponseEntity.ok("✅ Orden creada correctamente. Estado: " + orden.getEstado());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error al procesar la orden.");
        }
    }

    // Endpoint para traer todas las órdenes ejecutadas desde Alpaca
    @GetMapping("/ordenes-ejecutadas")
    public ResponseEntity<List<Map<String, Object>>> getOrdenesEjecutadas() {
        try {
            List<Map<String, Object>> ordenes = alpacaService.getOrdenesEjecutadas();
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para traer todas las órdenes pendientes desde Alpaca
    @GetMapping("/ordenes-pendientes")
    public ResponseEntity<List<Map<String, Object>>> getOrdenesPendientes() {
        try {
            List<Map<String, Object>> ordenes = alpacaService.getOrdenesPendientes();
            return ResponseEntity.ok(ordenes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint adicional para obtener operaciones ejecutadas desde Alpaca
    @GetMapping("/operaciones-ejecutadas")
    public ResponseEntity<?> obtenerOperacionesEjecutadas() {
        try {
            List<Map<String, Object>> operaciones = alpacaService.getExecutedOrdersFromAlpaca();
            return ResponseEntity.ok(operaciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudieron obtener las operaciones ejecutadas: " + e.getMessage()));
        }
    }

        }
    }


}
