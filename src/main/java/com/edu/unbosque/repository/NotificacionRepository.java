package com.edu.unbosque.repository;


import com.edu.unbosque.model.Accion;
import com.edu.unbosque.model.Notificacion;
import com.edu.unbosque.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // 🔔 Buscar todas las alertas activas
    List<Notificacion> findByActivaTrue();

    // 🔍 Buscar alertas activas de un usuario específico
    List<Notificacion> findByUsuarioAndActivaTrue(Usuario usuario);

    // 🔍 Buscar alertas activas para una acción específica
    List<Notificacion> findByAccionAndActivaTrue(Accion accion);
}