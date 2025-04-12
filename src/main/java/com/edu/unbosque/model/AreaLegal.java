package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "area_legal")
@Data
public class AreaLegal {

    @Id
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario; // Relación uno a uno con Usuario
}
