package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Usuario_Comisionista")
@IdClass(UsuarioComisionistaId.class)
@Data
public class Usuario_Comisionista {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_comisionista")
    private Comisionista comisionista;


}
