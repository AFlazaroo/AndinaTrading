package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "accion")
@Data
public class Accion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAccion;

    private String ticket;
    private String nombreCompania;
    private String sector;
    private Double precioActual;
    private Integer volumen;
    private Double capitalizacionMercado;

    @ManyToOne
    @JoinColumn(name = "id_mercado")
    private Mercado mercado;
}