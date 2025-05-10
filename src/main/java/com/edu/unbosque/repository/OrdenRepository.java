package com.edu.unbosque.repository;

import com.edu.unbosque.model.Orden;
import com.edu.unbosque.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Integer> {

    List<Orden> findByUsuario(Usuario usuario);
}