package com.edu.unbosque.controller;

import com.edu.unbosque.model.*;
import com.edu.unbosque.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/traders")
public class TraderController {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private AccionRepository accionRepository;

    @Autowired
    private TraderRepository traderRepository;

    @Autowired
    private MercadoRepository mercadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    @PostMapping("/alerta")
    public ResponseEntity<String> crearAlerta(
            @RequestParam int id_usuario,
            @RequestParam int id_accion,
            @RequestParam String tipoAlerta,
            @RequestParam double valorObjetivo,
            @RequestParam String canal) {

        System.out.println("ID Usuario recibido: " + id_usuario);
        System.out.println("ID Acción recibido: " + id_accion);


        Optional<Trader> traderOpt = traderRepository.findById(id_usuario);
        if (traderOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado o no es un Trader.");
        }

        Optional<Accion> accionOpt = accionRepository.findById(id_accion);
        if (accionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Acción no encontrada.");
        }

        if (!tipoAlerta.equalsIgnoreCase("mayor") && !tipoAlerta.equalsIgnoreCase("menor")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo de alerta inválido. Usa 'mayor' o 'menor'.");
        }

        // Verificar si la acción es nula
        if (accionOpt.get() == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("La acción asociada es nula.");
        }

        try {
            Notificacion notificacion = new Notificacion();
            notificacion.setTipoAlerta(tipoAlerta);
            notificacion.setValorObjetivo(valorObjetivo);
            notificacion.setCanal(canal);
            notificacion.setActiva(true);
            notificacion.setUsuario(traderOpt.get());
            notificacion.setAccion(accionOpt.get());  // Asegúrate de que esta acción esté asignada correctamente

            // Guarda la notificación
            notificacionRepository.save(notificacion);

            return ResponseEntity.ok("Alerta creada correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear la alerta.");
        }
    }


    @GetMapping("/debug/{id}")
    public ResponseEntity<?> debug(@PathVariable int id) {
        Trader trader = traderRepository.findById(id).orElse(null);
        if (trader == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trader no encontrado");
        return ResponseEntity.ok(trader.getNombre() + " con experiencia " + trader.getExperiencia());
    }

    @PostMapping("/crear-prueba")
    public ResponseEntity<String> crearTraderDePrueba() {
        try {
            Trader nuevoTrader = new Trader();
            nuevoTrader.setNombre("Carlos");
            nuevoTrader.setApellido("Gómez");
            nuevoTrader.setEmail("carlos.gomez@example.com");
            nuevoTrader.setTelefono("123456789");
            nuevoTrader.setPassword("supersecreto");
            nuevoTrader.setEstado("activo");
            nuevoTrader.setExperiencia(10);
            nuevoTrader.setRol(Rol.Trader); // <-- ¡Este es el fix importante!

            Trader guardado = traderRepository.save(nuevoTrader);

            return ResponseEntity.ok("Trader creado con ID: " + guardado.getIdUsuario());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el trader.");
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
            nueva.setActiva(true);
            nueva.setUsuario(usuarioRepository.findById(1).get());  // Suponiendo que el usuario existe
            nueva.setAccion(accionRepository.findById(2).get());  // Suponiendo que la acción existe

            notificacionRepository.save(nueva);
            return ResponseEntity.ok("Notificación creada con ID: " + nueva.getId_notificacion());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la notificación.");
        }
    }




}
