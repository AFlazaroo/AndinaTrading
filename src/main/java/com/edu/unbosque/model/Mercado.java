package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mercado")
@Data
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMercado;

    private String nombre;
    private String zonaHoraria;

    private String horarioApertura;
    private String horarioCierre;
}