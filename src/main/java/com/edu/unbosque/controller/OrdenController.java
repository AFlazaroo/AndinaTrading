package com.edu.unbosque.controller;

import com.edu.unbosque.model.Orden;
import com.edu.unbosque.model.Transaccion;
import com.edu.unbosque.repository.AccionRepository;
import com.edu.unbosque.repository.OrdenRepository;
import com.edu.unbosque.repository.UsuarioRepository;
import com.edu.unbosque.service.AlpacaService;
import com.edu.unbosque.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AccionRepository accionRepository;

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private AlpacaService alpacaService;

    // Endpoint para que el usuario ejecute una orden, Luego de que el comisionista la haya mandadox
    @PostMapping("/crear")
    public ResponseEntity<?> ejecutarOrden(@PathVariable Integer idOrden) {
        try {
            // Obtener la orden
            Optional<Orden> ordenOpt = ordenRepository.findById(idOrden);
            if (ordenOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Orden no encontrada.");
            }

            Orden orden = ordenOpt.get();

            // Verificar si la orden está pendiente
            if (!"PENDIENTE".equals(orden.getEstado())) {
                return ResponseEntity.status(400).body("La orden no está en estado pendiente.");
            }

            // Cambiar el estado de la orden a ejecutada
            orden.setEstado("EJECUTADA");
            orden.setFecha_ejecucion(LocalDateTime.now());
            ordenRepository.save(orden);

            // Registrar la transacción (esto genera la comisión y el procesamiento)
            Transaccion transaccion = transaccionService.registrarTransaccion(orden);
            orden.setTransaccion(transaccion);
            ordenRepository.save(orden);

            return ResponseEntity.ok("Orden ejecutada exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al ejecutar la orden.");
        }
    }

    // Un solo endpoint para aprobar o rechazar la orden
    @PostMapping("/manejarOrden")
    public ResponseEntity<?> manejarOrden(
            @RequestParam Integer idOrden,   // ID de la orden que el usuario va a aprobar o rechazar
            @RequestParam boolean aceptarOrden) {   // true si acepta, false si rechaza
        try {
            // Busca la orden por ID
            Optional<Orden> ordenOpt = ordenRepository.findById(idOrden);
            if (ordenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Orden no encontrada");
            }

            Orden orden = ordenOpt.get();

            // Verifica que la orden esté en estado pendiente
            if (!orden.getEstado().equals("PENDIENTE")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La orden no está en estado pendiente.");
            }

            // Si acepta la orden
            if (aceptarOrden) {
                // Actualiza el estado de la orden a ejecutada
                orden.setEstado("EJECUTADA");

                // Obtiene el precio actual de la acción
                double precioActual = alpacaService.getPrecioActualDesdeAlpaca(orden.getAccion().getTicket());
                orden.setPrecio(precioActual);
                orden.setFecha_ejecucion(LocalDateTime.now());

                ordenRepository.save(orden);

                // Registra la transacción (esto sería ejecutar la compra de las acciones)
                Transaccion transaccion = transaccionService.registrarTransaccion(orden);
                orden.setTransaccion(transaccion);
                ordenRepository.save(orden);

                return ResponseEntity.ok("✅ Orden ejecutada correctamente. Transacción registrada.");
            } else {
                // Si rechaza la orden
                orden.setEstado("RECHAZADA");
                orden.setFecha_ejecucion(null);
                ordenRepository.save(orden);

                return ResponseEntity.ok("✅ Orden rechazada.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error al procesar la orden.");
        }
    }
}

