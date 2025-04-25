package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "holding")
@Data
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_holding;

    private LocalDateTime fecha_compra;
    private Double precio_compra;
    private Double precio_actual;
    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "id_portafolio")
    private Portafolio portafolio;
}