package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orden")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_orden")
    private Integer idOrden;

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