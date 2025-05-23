package com.edu.unbosque.controller;

import com.edu.unbosque.config.AppConfig;
import com.edu.unbosque.config.TokenAdmin;
import com.edu.unbosque.model.*;
import com.edu.unbosque.repository.*;
import com.edu.unbosque.service.CorreosService;
import com.edu.unbosque.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger log = LogManager.getLogger(UsuarioController.class);
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenAdmin tokenAdmin;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private AccionRepository accionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private OrdenRepository ordenRepository;
    @Autowired
    private CorreosService correosService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HoldingRepository holdingRepository;


    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(
            @RequestParam int id_usuario,
            @RequestParam int id_orden,
            @RequestParam String tipoAlerta,
            @RequestParam double valorObjetivo,
            @RequestParam String canal) {

        System.out.println("ID Usuario recibido: " + id_usuario);
        System.out.println("ID Orden recibido: " + id_orden);


        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id_usuario);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado o no es un Trader.");
        }

        Optional<Orden> ordenOpt = ordenRepository.findById(id_orden);
        if (ordenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no encontrada.");
        }

        if (!tipoAlerta.equalsIgnoreCase("mayor") && !tipoAlerta.equalsIgnoreCase("menor")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de alerta inválido. Usa 'mayor' o 'menor'.");
        }

        // Verificar si la acción es nula
        if (ordenOpt.get() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("La Orden asociada es nula.");
        }

        try {
            Notificacion notificacion = new Notificacion();
            notificacion.setTipoAlerta(tipoAlerta);
            notificacion.setValorObjetivo(valorObjetivo);
            notificacion.setCanal(canal);
            notificacion.setEstado(true);
            notificacion.setUsuario(usuarioOpt.get());
            notificacion.setOrden(ordenOpt.get());  // Asegúrate de que esta acción esté asignada correctamente

            // Guarda la notificación
            notificacionRepository.save(notificacion);

            return ResponseEntity.ok("Alerta creada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear la alerta.");
        }
    }


    @PostMapping("/crear-accion-prueba")
    public ResponseEntity<String> crearAccionDePrueba() {
        try {
            Accion nuevaAccion = new Accion();
            nuevaAccion.setTicket("AAPL");
            nuevaAccion.setNombreCompania("Apple Inc.");
            nuevaAccion.setSector("Tecnología");
            nuevaAccion.setPrecioActual(150.0);
            nuevaAccion.setVolumen(1000000);
            nuevaAccion.setCapitalizacionMercado(2500000000.0);

            // Busca el mercado al que la quieres asociar (usa uno que exista en tu BD)
            Optional<Mercado> mercadoOpt = mercadoRepository.findById(1); // Cambia el ID si es necesario
            if (mercadoOpt.isPresent()) {
                nuevaAccion.setMercado(mercadoOpt.get());
            }

            Accion guardada = accionRepository.save(nuevaAccion);

            return ResponseEntity.ok("Acción creada con ID: " + guardada.getId_accion());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la acción.");
        }
    }
    @Transactional
    @PostMapping("/crear-notificacion")
    public ResponseEntity<String> crearNotificacionDePrueba() {
        try {
            Notificacion nueva = new Notificacion();
            nueva.setTipoAlerta("mayor");
            nueva.setValorObjetivo(150.0);
            nueva.setCanal("email");
            nueva.setEstado(true);
            nueva.setUsuario(usuarioRepository.findById(1).get());  // Suponiendo que el usuario existe
            nueva.setOrden(ordenRepository.findById(2).get());  // Suponiendo que la acción existe

            notificacionRepository.save(nueva);
            return ResponseEntity.ok("Notificación creada con ID: " + nueva.getId_notificacion());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la notificación.");
        }
    }

    @GetMapping("/listadoUsuarios")
    public ResponseEntity<List<Usuario>> listadoUsuario(@RequestParam int idUsuarioLogeado) {
        List<Usuario> usuarios = usuarioService.listadoGeneralUsuariosFiltro(idUsuarioLogeado);
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/registroUsuario")
    public ResponseEntity<String> registroDeUsuario(@Valid @RequestBody Usuario usuario){
        Optional<Usuario> usuarioGuardado = usuarioService.guardarUsuario(usuario);

        if (usuarioGuardado.isPresent()) {
            return ResponseEntity.ok("Usuario registrado exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe.");
        }
    }

    @PutMapping("/editarContrasena")
    public ResponseEntity<String> cambiarContrasena(@RequestParam String sesionUsuario,
                                                    @RequestBody String nuevaContrasena ){
        int idusuario  = Integer.parseInt(tokenAdmin.validarTokenIdentificadorUsuario(sesionUsuario));
        log.info("usuario: {}", idusuario);
        log.info("nuevaContrasena: {}", nuevaContrasena.hashCode());

        if (usuarioService.existeUsuario(idusuario)){
            log.info("si existe el usuario: {}", idusuario);
            usuarioService.actualizarCredencialesUsuario(idusuario, nuevaContrasena);
            return ResponseEntity.ok("Contrasena actualizada exitosamente.");
        }
        log.info("si no existe el usuario: {}", passwordEncoder.encode(nuevaContrasena));
        return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDatosDashboard(@PathVariable int id) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            Usuario usuario = usuarioOpt.get();
            Portafolio portafolio = usuario.getPortafolio();

            if (portafolio == null) {
                return ResponseEntity.badRequest().body("El usuario no tiene portafolio asignado.");
            }

            List<Holding> holdings = holdingRepository.findByPortafolio_IdPortafolio(portafolio.getIdPortafolio());

            double valorUSD = holdings.stream()
                    .mapToDouble(h -> h.getCantidad() * h.getPrecio_actual())
                    .sum();

            double valorCOP = valorUSD * 3850;

            List<Orden> operaciones = ordenRepository.findByUsuario(usuario);

            Map<String, Object> response = Map.of(
                    "nombre", usuario.getNombre(),
                    "apellido", usuario.getApellido(),
                    "valorPortafolio", Map.of("usd", valorUSD, "cop", valorCOP),
                    "holdings", holdings.stream().map(h -> Map.of(
                            "accion", h.getAccion() != null ? h.getAccion().getTicket() : "N/A",
                            "nombreCompania", h.getAccion() != null ? h.getAccion().getNombreCompania() : "Desconocida",
                            "cantidad", h.getCantidad(),
                            "precio_actual", h.getPrecio_actual(),
                            "valor_total", h.getCantidad() * h.getPrecio_actual()
                    )).toList(),
                    "operaciones", operaciones.stream().map(o -> Map.of(
                            "accion", o.getAccion() != null ? o.getAccion().getTicket() : "N/A",
                            "tipo", o.getTipo_orden(),
                            "fecha", o.getFecha_ejecucion()
                    )).toList()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // 🟠 Ver en consola cuál es la excepción exacta
            return ResponseEntity.badRequest().body("Error interno: " + e.getMessage());
        }
    }

    @GetMapping("/debug/holdings/{idPortafolio}")
    public ResponseEntity<?> debugHoldings(@PathVariable Integer idPortafolio) {
        List<Holding> holdings = holdingRepository.findByPortafolio_IdPortafolio(idPortafolio);
        System.out.println("Holdings cargados: " + holdings.size());
        return ResponseEntity.ok(holdings);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Map<String, String> loginRequest) {
        String correo = loginRequest.get("correo");
        String contrasena = loginRequest.get("contrasena");

        boolean credencialesValidas = usuarioService.validarCredenciales(correo, contrasena);

        if (credencialesValidas) {
            Optional<Usuario> usuarioOpt = usuarioService.encontrarUsuarioCorreo(correo);
            if (usuarioOpt.isPresent()) {
                return ResponseEntity.ok(usuarioOpt.get().getRol()); // 🔥 Solo devuelve el rol como string
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

    @PostMapping("/asociarComisionista")
    public ResponseEntity<?> AsociarComisionista(@RequestParam int idComisionistaSeleccionado ,
                                                 @RequestParam int idUsuarioLogeado) {
        usuarioService.asociarUsuarioComisionista(idComisionistaSeleccionado,idUsuarioLogeado);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UsuarioAsociadoCorrectamente");
    }


}
