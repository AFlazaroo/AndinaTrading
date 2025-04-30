package com.edu.unbosque.controller;

import com.edu.unbosque.model.*;
import com.edu.unbosque.repository.*;
import com.edu.unbosque.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

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

    @PostMapping("/registroUsuario")
    public ResponseEntity<String> registroDeUsuario(@Valid @RequestBody Usuario usuario){
        Optional<Usuario> usuarioGuardado = usuarioService.guardarUsuario(usuario);

        if (usuarioGuardado.isPresent()) {
            return ResponseEntity.ok("Usuario registrado exitosamente.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe.");
        }
    }




}
