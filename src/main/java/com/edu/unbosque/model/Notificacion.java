package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;



import java.util.Collection;
import java.util.List;
@Entity
@Table(name = "notificacion")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id_notificacion;

    @Column(name = "tipo_alerta")
    private String tipoAlerta;

    @Column(name = "valor_objetivo")
    private Double valorObjetivo;

    private String canal;
    private boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    private Orden orden;

}
