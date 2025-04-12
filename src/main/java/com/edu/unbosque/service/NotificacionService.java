package com.edu.unbosque.service;


import com.edu.unbosque.model.Notificacion;
import com.edu.unbosque.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {
/*
    @Autowired
    private NotificacionRepository notificacionRepository;

    // Crear una nueva notificación
    public Notificacion crearNotificacion(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    // Activar una notificación
    public Notificacion activarNotificacion(Integer id) {
        Optional<Notificacion> notificacionOpt = notificacionRepository.findById(id);
        if (notificacionOpt.isPresent()) {
            Notificacion notificacion = notificacionOpt.get();
            notificacion.setActiva(true);
            return notificacionRepository.save(notificacion);
        } else {
            throw new RuntimeException("Notificación no encontrada");
        }
    }

    // Desactivar una notificación
    public Notificacion desactivarNotificacion(Integer id) {
        Optional<Notificacion> notificacionOpt = notificacionRepository.findById(id);
        if (notificacionOpt.isPresent()) {
            Notificacion notificacion = notificacionOpt.get();
            notificacion.setActiva(false);
            return notificacionRepository.save(notificacion);
        } else {
            throw new RuntimeException("Notificación no encontrada");
        }
    }

    // Obtener todas las notificaciones activas para un usuario
    public List<Notificacion> obtenerNotificacionesPorUsuario(Integer usuarioId) {
        return notificacionRepository.findByUsuarioId(usuarioId);
    }

    // Verificar si un precio ha alcanzado el valor objetivo para enviar la notificación
    public void verificarNotificaciones(Double precioAccion, Integer usuarioId) {
        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioId(usuarioId);
        for (Notificacion notificacion : notificaciones) {
            if (notificacion.getValorObjetivo() <= precioAccion && notificacion.getActiva()) {
                enviarNotificacion(notificacion);
            }
        }
    }

    // Método simulado para enviar la notificación (puedes integrar correo, SMS, etc.)
    private void enviarNotificacion(Notificacion notificacion) {
        // Aquí podrías integrar un servicio real de notificaciones, como Email, SMS, o Push.
        System.out.println("Enviando notificación a " + notificacion.getUsuario().getEmail() +
                ": " + notificacion.getTipoAlerta());

 */
    }
