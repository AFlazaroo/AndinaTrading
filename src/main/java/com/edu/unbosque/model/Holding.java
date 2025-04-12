package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "holding")
@Data
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHolding;

    private String fechaCompra;
    private Double precioCompra;
    private Double precioActual;
    private Integer numeroAcciones;

    @ManyToOne
    @JoinColumn(name = "id_portafolio")
    private Portafolio portafolio;

    @ManyToOne
    @JoinColumn(name = "id_accion")
    private Accion accion;
}