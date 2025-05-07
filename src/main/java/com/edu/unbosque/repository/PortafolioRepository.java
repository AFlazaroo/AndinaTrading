package com.edu.unbosque.repository;

import com.edu.unbosque.model.Orden;
import com.edu.unbosque.model.Portafolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortafolioRepository extends JpaRepository<Portafolio, Integer> {
}
