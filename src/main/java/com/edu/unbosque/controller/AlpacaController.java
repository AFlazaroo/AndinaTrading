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
        public AlpacaController(AlpacaService alpacaService, UsuarioRepository usuarioRepository, AccionRepository accionRepository, NotificacionRepository notificacionRepository, UsuarioService usuarioService, OrdenService ordenService, OrdenRepository ordenRepository, TransaccionService transaccionService, TransaccionRepository transaccionRepository, MercadoRepository mercadoRepository) {
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

        // Endpoint para obtener el balance de la cuenta
        @GetMapping("/balance")
        public String getBalance() {
            return alpacaService.getAccountBalance();
        }

        // Endpoint para obtener la cotización de una acción
        @GetMapping("/quote/{symbol}")
        public Map<String, Object> getQuote(@PathVariable String symbol) {
            return alpacaService.getStockQuote(symbol);
        }

        // Endpoint para realizar una orden de compra
        @PostMapping("/buy")
        public String placeBuyOrder(@RequestParam String symbol, @RequestParam int qty, @RequestParam double price) {
            return alpacaService.placeBuyOrder(symbol, qty, price);
        }

        // Endpoint para obtener las posiciones abiertas (portafolio)
        @GetMapping("/positions")
        public String getOpenPositions() {
            return alpacaService.getOpenPositions();
        }


        @GetMapping("/assets")
        public String getAssets() {
            return alpacaService.getAvailableAssets();
        }

            @PostMapping("/alerta")
            public ResponseEntity<String> crearAlerta(
                    @RequestParam int id_usuario,
                @RequestParam int id_orden,
                @RequestParam String tipoAlerta,
                @RequestParam double valorObjetivo,
                @RequestParam String canal) {

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id_usuario);
            Optional<Orden> ordenOpt = ordenRepository.findById(id_orden);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            if (ordenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acción no encontrada.");
            }

            try {
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
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear la alerta.");
            }
        }

        // Endpoint para obtener datos de velas japonesas (historical candles)
        @GetMapping("/historical/{symbol}/{timeFrame}")
        public ResponseEntity<?> getHistoricalData(
                @PathVariable String symbol,
                @PathVariable String timeFrame,
                @RequestParam String start,
                @RequestParam String end) {
            try {
                List<Map<String, Object>> candles = alpacaService.getHistoricalCandles(symbol, timeFrame, start, end);
                return ResponseEntity.ok(candles);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
            }
        }
        // Endpoint para orden tipo MARKET
        @PostMapping("/order/market")
        public ResponseEntity<?> placeMarketOrder(
                @RequestParam String symbol,
                @RequestParam int qty,
                @RequestParam String side,
                @RequestParam Integer idUsuario) {

            try {
                // 1. Verifica usuario
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
                if (usuarioOpt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
                }

                // 2. Busca la acción por ticket
                Accion accion = accionRepository.findByTicket(symbol)
                        .orElseThrow(() -> new RuntimeException("Acción no encontrada con ticket: " + symbol));

                // 3. Obtener el mercado asociado a la acción
                Mercado mercado = accion.getMercado();

                // 4. Obtener el precio actual desde la API de Alpaca
                double precioActual = alpacaService.getPrecioActualDesdeAlpaca(symbol);
                if (precioActual <= 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo obtener el precio actual del símbolo.");
                }

                // 5. Actualiza el precio_actual de la acción
                accion.setPrecioActual(precioActual);
                accionRepository.save(accion);

                // 6. Obtener la hora actual en la zona horaria del mercado
                ZoneId zonaMercado = ZoneId.of(alpacaService.convertirZonaAHorariaJava(mercado.getZona_horario()));
                ZonedDateTime ahoraEnMercado = ZonedDateTime.now(zonaMercado);
                LocalTime horaActual = ahoraEnMercado.toLocalTime();

                // 7. Comparar si la hora actual está dentro del horario del mercado
                boolean dentroHorario = horaActual.isAfter(LocalTime.from(mercado.getHorario_apertura())) &&
                        horaActual.isBefore(LocalTime.from(mercado.getHorario_cierre()));

                // 8. Crear y guardar la orden local
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

                // 9. Registrar transacción solo si fue ejecutada
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




        // Endpoint para orden tipo STOP LOSS
        @PostMapping("/order/stoploss")
        public String placeStopLossOrder(
                @RequestParam String symbol,
                @RequestParam int qty,
                @RequestParam String side,
                @RequestParam double stopPrice) {
            return alpacaService.placeStopLossOrder(symbol, qty, side, stopPrice);
        }

        // Endpoint para orden tipo TAKE PROFIT
        @PostMapping("/order/takeprofit")
        public String placeTakeProfitOrder(
                @RequestParam String symbol,
                @RequestParam int qty,
                @RequestParam String side,
                @RequestParam double limitPrice) {
            return alpacaService.placeTakeProfitOrder(symbol, qty, side, limitPrice);
        }


    }
                                            








