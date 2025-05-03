package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "mercado")
@Data
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_mercado;

    private String nombre;
    private String zona_horario;

    @Column(name = "horario_apertura")
    private LocalTime horario_apertura;

    @Column(name = "horario_cierre")
    private LocalTime horario_cierre;

    @OneToMany(mappedBy = "mercado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Accion> acciones;
}
