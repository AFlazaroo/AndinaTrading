package com.edu.unbosque.repository;



import com.edu.unbosque.model.Accion;
import com.edu.unbosque.model.Mercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Integer> {

    Optional<Mercado> findByAcciones(Accion accion);

}

