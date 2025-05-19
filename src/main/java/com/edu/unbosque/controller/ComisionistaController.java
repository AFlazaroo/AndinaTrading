package com.edu.unbosque.controller;


import com.edu.unbosque.model.Accion;
import com.edu.unbosque.model.Comisionista;
import com.edu.unbosque.model.Orden;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.repository.AccionRepository;
import com.edu.unbosque.repository.OrdenRepository;
import com.edu.unbosque.repository.UsuarioRepository;
import com.edu.unbosque.service.AlpacaService;
import com.edu.unbosque.service.ComisionistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comisionista")
public class ComisionistaController {
    private final ComisionistaService comisionistaService;
    private final UsuarioRepository usuarioRepository;
    private final AccionRepository accionRepository;
    private final OrdenRepository ordenRepository;
    private final AlpacaService alpacaService;

    @Autowired
    public ComisionistaController(ComisionistaService comisionistaService, UsuarioRepository usuarioRepository, AccionRepository accionRepository, OrdenRepository ordenRepository, AlpacaService alpacaService) {
        this.comisionistaService = comisionistaService;
        this.usuarioRepository = usuarioRepository;
        this.accionRepository = accionRepository;
        this.ordenRepository = ordenRepository;
        this.alpacaService = alpacaService;
    }

    @GetMapping("/disponibles")
    public List<Comisionista> obtenerComisionistasDisponibles() {
        return comisionistaService.listarComisionistasActivos();
    }

    // Endpoint para desvincular al usuario de un comisionista.
    // usuario con ID "" y el comisionista con ID "". http://localhost:8080/comisionista/desvincular/1/2.

    @DeleteMapping("/desvincular/{idUsuario}/{idComisionista}")
    public ResponseEntity<String> desvincularUsuarioDeComisionista(@PathVariable Integer idUsuario,
                                                                   @PathVariable Integer idComisionista) {
        boolean desvinculado = comisionistaService.desvincularUsuarioDeComisionista(idUsuario, idComisionista);

        if (desvinculado) {
            return ResponseEntity.ok("Usuario desvinculado correctamente del Comisionista.");
        } else {
            return ResponseEntity.status(404).body("Usuario o Comisionista no encontrado.");
        }
    }

    @PostMapping("/crearOrden")
    public ResponseEntity<?> crearOrdenPorRecomendacion(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idComisionista,
            @RequestParam String symbol,
            @RequestParam int qty) {
        try {
            // Verifica si el usuario existe
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Busca la acción por su símbolo (ticket)
            Accion accion = accionRepository.findFirstByTicket(symbol)
                    .orElseThrow(() -> new RuntimeException("Acción no encontrada con ticket: " + symbol));

            // Obtiene el precio actual de la acción
            double precioActual = alpacaService.getPrecioActualDesdeAlpaca(symbol);
            if (precioActual <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se pudo obtener el precio actual del símbolo.");
            }

            // Crea la orden, pero no la ejecuta aún
            Orden orden = new Orden();
            orden.setAccion(accion);
            orden.setUsuario(usuarioOpt.get());
            orden.setCantidad(qty);
            orden.setPrecio(precioActual);
            orden.setTipo_orden("MARKET");
            orden.setFecha_creacion(LocalDateTime.now());
            orden.setUltima_modificacion(LocalDateTime.now());
            orden.setEstado("PENDIENTE");  // Estado pendiente porque el usuario aún no ha aprobado
            orden.setFecha_ejecucion(null);

            ordenRepository.save(orden);

            // Devuelve respuesta indicando que la orden fue creada correctamente pero pendiente
            return ResponseEntity.ok("✅ Orden creada correctamente. Esperando aprobación del usuario.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error al crear la orden.");
        }
    }
}


