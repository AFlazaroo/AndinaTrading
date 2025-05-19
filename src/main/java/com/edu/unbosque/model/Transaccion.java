package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaccion")
@Data
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    private Integer id_transaccion;
    private Double comision_comisionista;
    private Double comision_sistema;
    private Double monto;
    private Double comision_total;
    private LocalDateTime fecha;

    @OneToOne
    @JoinColumn(name = "id_orden")
    private Orden orden;

    @ManyToOne
    @JoinColumn(name = "id_comisionista")
    private Comisionista comisionista;

}