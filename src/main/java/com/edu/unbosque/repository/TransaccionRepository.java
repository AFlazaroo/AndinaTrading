package com.edu.unbosque.repository;

import com.edu.unbosque.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {
    List<Transaccion> findByOrden_Usuario_IdUsuario(Integer idUsuario);
    List<Transaccion> findByOrden_IdOrden(Integer idOrden);



}
