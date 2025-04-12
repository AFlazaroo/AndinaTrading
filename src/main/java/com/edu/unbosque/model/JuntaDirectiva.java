package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "junta_directiva")
@Data
public class JuntaDirectiva {

    @Id
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // Relación uno a uno con Usuario
}