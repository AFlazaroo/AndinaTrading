package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orden")
@Data
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idOrden;

    private String tipoOrden;
    private String fechaCreacion;
    private String ultimaModificacion;
    private String estado;
    private Double precio;
    private Integer cantidad;
    private String fechaEjecucion;

    @ManyToOne
    @JoinColumn(name = "id_trader")
    private Trader trader;

    @ManyToOne
    @JoinColumn(name = "id_accion")
    private Accion accion;

    @ManyToOne
    @JoinColumn(name = "id_notificacion")
    private Notificacion notificacion;
}