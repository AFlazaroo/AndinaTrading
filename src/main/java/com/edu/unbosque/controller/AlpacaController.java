package com.edu.unbosque.controller;

import com.edu.unbosque.repository.AccionRepository;
import com.edu.unbosque.repository.NotificacionRepository;
import com.edu.unbosque.repository.OrdenRepository;
import com.edu.unbosque.repository.UsuarioRepository;
import com.edu.unbosque.service.AlpacaService;
import com.edu.unbosque.service.OrdenService;
import com.edu.unbosque.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public AlpacaController(AlpacaService alpacaService, UsuarioRepository usuarioRepository,
                            AccionRepository accionRepository, NotificacionRepository notificacionRepository,
                            UsuarioService usuarioService, OrdenService ordenService, OrdenRepository ordenRepository) {
        this.alpacaService = alpacaService;
        this.usuarioRepository = usuarioRepository;
        this.accionRepository = accionRepository;
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
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

    // === 4. Gestión de Órdenes ===
    @PostMapping("/order/market")
    public String placeMarketOrder(@RequestParam String symbol, @RequestParam int qty, @RequestParam String side) {
        return alpacaService.placeMarketOrder(symbol, qty, side);
    }

    @PostMapping("/buy")
    public String placeBuyOrder(@RequestParam String symbol, @RequestParam int qty, @RequestParam double price) {
        return alpacaService.placeBuyOrder(symbol, qty, price);
    }

    @PostMapping("/order/stoploss")
    public String placeStopLossOrder(@RequestParam String symbol, @RequestParam int qty,
                                     @RequestParam String side, @RequestParam double stopPrice) {
        return alpacaService.placeStopLossOrder(symbol, qty, side, stopPrice);
    }

    @PostMapping("/order/takeprofit")
    public String placeTakeProfitOrder(@RequestParam String symbol, @RequestParam int qty,
                                       @RequestParam String side, @RequestParam double limitPrice) {
        return alpacaService.placeTakeProfitOrder(symbol, qty, side, limitPrice);
    }

    // === 5. Gestión de Alertas ===
    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(@RequestParam int id_usuario, @RequestParam int id_orden,
                                              @RequestParam String tipoAlerta, @RequestParam double valorObjetivo,
                                              @RequestParam String canal) {
        try {
            if (usuarioRepository.findById(id_usuario).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            if (ordenRepository.findById(id_orden).isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no encontrada.");

            boolean creada = alpacaService.crearAlerta(id_usuario, id_orden, tipoAlerta, valorObjetivo, canal);
            return creada ? ResponseEntity.ok("Alerta creada correctamente.") :
                    ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo crear alerta.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }
}
