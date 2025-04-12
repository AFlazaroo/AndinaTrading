package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "portafolio")
@Data
public class Portafolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPortafolio;

    private String fechaCreacion;

    @OneToOne
    @JoinColumn(name = "id_trader")
    private Trader trader;
}