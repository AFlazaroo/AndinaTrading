package com.edu.unbosque.repository;

import com.edu.unbosque.model.Holding;
import com.edu.unbosque.model.Portafolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Integer> {
    List<Holding> findByPortafolio_IdPortafolio(Integer idPortafolio);
}