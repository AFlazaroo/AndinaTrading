package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "comisionista")
@Data
public class Comisionista {

    @Id
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // Relación uno a uno con Usuario

    private Integer experiencia;
}
