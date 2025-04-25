package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "mercado")
@Data
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_mercado;

    private String nombre;
    private String zona_horario;

    private LocalDateTime  horario_apertura;
    private LocalDateTime horario_cierre;


    @ManyToOne
    @JoinColumn(name = "id_accion")
    private Accion accion;

    }