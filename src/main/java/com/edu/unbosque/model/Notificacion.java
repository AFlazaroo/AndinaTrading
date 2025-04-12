package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "notificacion")
@Data
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotificacion;

    private String tipoAlerta;
    private Double valorObjetivo;
    private String canal;
    private Boolean activa;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}