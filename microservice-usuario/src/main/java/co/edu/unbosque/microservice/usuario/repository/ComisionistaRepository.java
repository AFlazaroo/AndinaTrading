package co.edu.unbosque.microservice.usuario.repository;
import co.edu.unbosque.microservice.usuario.model.Comisionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComisionistaRepository extends JpaRepository<Comisionista, Integer> {

    List<Comisionista> findByEstadoTrue();
}
