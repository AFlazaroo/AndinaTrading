    package com.edu.unbosque.controller;

    import com.edu.unbosque.model.Accion;
    import com.edu.unbosque.model.Notificacion;
    import com.edu.unbosque.model.Usuario;
    import com.edu.unbosque.repository.AccionRepository;
    import com.edu.unbosque.repository.NotificacionRepository;
    import com.edu.unbosque.repository.UsuarioRepository;
    import com.edu.unbosque.service.AlpacaService;
    import com.edu.unbosque.service.TraderService;
    import com.edu.unbosque.service.UsuarioService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.Optional;

    @RestController
    @RequestMapping("/api/alpaca")
    public class AlpacaController {

        private final AlpacaService alpacaService;
        private final UsuarioRepository usuarioRepository;
        private final AccionRepository accionRepository;
        private final NotificacionRepository notificacionRepository;
        private final UsuarioService usuarioService;
        private final TraderService traderService;



        @Autowired
        public AlpacaController(AlpacaService alpacaService, UsuarioRepository usuarioRepository, AccionRepository accionRepository, NotificacionRepository notificacionRepository, UsuarioService usuarioService, TraderService traderService) {
            this.alpacaService = alpacaService;
            this.usuarioRepository = usuarioRepository;
            this.accionRepository = accionRepository;
            this.notificacionRepository = notificacionRepository;
            this.usuarioService = usuarioService;
            this.traderService = traderService;
        }

        // Endpoint para obtener el balance de la cuenta
        @GetMapping("/balance")
        public String getBalance() {
            return alpacaService.getAccountBalance();
        }

        // Endpoint para obtener la cotización de una acción
        @GetMapping("/quote/{symbol}")
        public String getQuote(@PathVariable String symbol) {
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
                @RequestParam int id_accion,
                @RequestParam String tipoAlerta,
                @RequestParam double valorObjetivo,
                @RequestParam String canal) {

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id_usuario);
            Optional<Accion> accionOpt = accionRepository.findById(id_accion);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            if (accionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acción no encontrada.");
            }

            try {
                Notificacion notificacion = new Notificacion();
                notificacion.setTipoAlerta(tipoAlerta);
                notificacion.setValorObjetivo(valorObjetivo);
                notificacion.setCanal(canal);
                notificacion.setActiva(true);
                notificacion.setUsuario(usuarioOpt.get());
                notificacion.setAccion(accionOpt.get());

                notificacionRepository.save(notificacion);
                return ResponseEntity.ok("Alerta creada correctamente.");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear la alerta.");
            }
        }


        }
                                            








