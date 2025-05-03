package com.edu.unbosque.repository;

import com.edu.unbosque.model.Comisionista;
import com.edu.unbosque.model.Usuario;
import com.edu.unbosque.model.Usuario_Comisionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioComisionistaRepository extends JpaRepository<Usuario_Comisionista, Integer> {
    List<Usuario_Comisionista> findByUsuario_IdUsuario(Integer idUsuario);
    void deleteByUsuarioAndComisionista(Usuario usuario, Comisionista comisionista);



}
