package com.edu.unbosque.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "portafolio")
@Data
public class Portafolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_portafolio;

    private LocalDate fecha_creacion;

    @OneToOne
    @JoinColumn(name = "id_usuario")
    @JsonBackReference
    private Usuario usuario;


   }