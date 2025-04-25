package com.edu.unbosque.repository;


import com.edu.unbosque.model.Accion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccionRepository extends JpaRepository<Accion, Integer> {

    // Buscar por ticket (ej: AAPL, MSFT)
    Optional<Accion> findByTicket(String ticket);

    // Buscar por nombre de la compañía
    List<Accion> findByNombreCompaniaContainingIgnoreCase(String nombre);
}