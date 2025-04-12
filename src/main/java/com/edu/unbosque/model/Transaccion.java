package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "transaccion")
@Data
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTransaccion;

    private Double monto;
    private Double comisionTotal;
    private Double comisionSistema;
    private Double comisionComisionista;
    private String fecha;

    @OneToOne
    @JoinColumn(name = "id_orden")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "id_comisionista")
    private Comisionista comisionista;
}