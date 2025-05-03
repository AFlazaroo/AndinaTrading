package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "accion")
@Data
public class Accion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    private Integer id_accion;

    @Column(name = "ticket")
    private String ticket;

    @Column(name = "nombre_compania")
    private String nombreCompania;

    @Column(name = "sector")
    private String sector;

    @Column(name = "precio_actual")
    private Double precioActual;

    @Column(name = "volumen")
    private Integer volumen;

    @Column(name = "capitalizacion_mercado")
    private Double capitalizacionMercado;

    @ManyToOne
    @JoinColumn(name = "id_mercado", nullable = false)
    private Mercado mercado;
}
