package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "comisionista")
@Data
public class Comisionista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comisionista")
    private Integer idComisionista;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String password;
    private boolean estado;
}