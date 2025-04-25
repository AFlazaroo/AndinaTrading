package com.edu.unbosque.repository;

import com.edu.unbosque.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar por correo (útil si haces login o validaciones)
    Optional<Usuario> findByEmail(String email);

    // Buscar por nombre (ejemplo)
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    Optional<Usuario> findById(Integer idUsuario);

    @Query(value = "SELECT * FROM usuario WHERE id_usuario = ?1", nativeQuery = true)
    Usuario encontrarUsuarioBasicoPorId(int id);


}