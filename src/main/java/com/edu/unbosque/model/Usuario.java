package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "usuario")
@Data
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // este sí se mantiene con snake_case porque es el nombre en BD
    private Integer idUsuario;

    private String nombre;
    private String apellido;

    private String email;
    private String telefono;
    private String password;
    private boolean estado;

    private String rol;

    @OneToOne
    @JoinColumn(name = "id_portafolio")
    private Portafolio portafolio;

}



