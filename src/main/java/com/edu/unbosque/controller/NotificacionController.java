package com.edu.unbosque.controller;

import com.edu.unbosque.model.Notificacion;
import com.edu.unbosque.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
/*
    @Autowired
    private NotificacionService notificacionService;

    // Crear una nueva notificación
    @PostMapping("/crear")
    public ResponseEntity<Notificacion> crearNotificacion(@RequestBody Notificacion notificacion) {
        Notificacion nuevaNotificacion = notificacionService.crearNotificacion(notificacion);
        return ResponseEntity.ok(nuevaNotificacion);
    }

    // Activar una notificación
    @PutMapping("/activar/{id}")
    public ResponseEntity<Notificacion> activarNotificacion(@PathVariable Integer id) {
        Notificacion notificacion = notificacionService.activarNotificacion(id);
        return ResponseEntity.ok(notificacion);
    }

    // Desactivar una notificación
    @PutMapping("/desactivar/{id}")
    public ResponseEntity<Notificacion> desactivarNotificacion(@PathVariable Integer id) {
        Notificacion notificacion = notificacionService.desactivarNotificacion(id);
        return ResponseEntity.ok(notificacion);
    }

    // Obtener notificaciones activas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(@PathVariable Integer usuarioId) {
        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(notificaciones);
    }

 */
}