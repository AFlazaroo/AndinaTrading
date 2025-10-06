package co.edu.unbosque.microservice.usuario.repository;

import co.edu.unbosque.microservice.usuario.model.Usuario_Comisionista;
import co.edu.unbosque.microservice.usuario.model.Comisionista;
import co.edu.unbosque.microservice.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;  
import java.util.Optional;

@Repository
public interface UsuarioComisionistaRepository extends JpaRepository<Usuario_Comisionista, Integer> {
    List<Usuario_Comisionista> findByUsuario_IdUsuario(Integer idUsuario);
    void deleteByUsuarioAndComisionista(Usuario usuario, Comisionista comisionista);



}
