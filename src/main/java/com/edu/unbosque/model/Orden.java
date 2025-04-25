package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orden")
@Data
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_orden;

    private String tipo_orden;
    private LocalDateTime fecha_creacion;
    private LocalDateTime  ultima_modificacion;
    private String estado;
    private Double precio;
    private Integer cantidad;
    private LocalDateTime  fecha_ejecucion;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_accion")
    private Accion accion;

    @OneToOne
    @JoinColumn(name = "id_transaccion")
    private Transaccion transaccion;

}