package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "suscripcion")
@Data
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSuscripcion;

    private String fechaInicio;
    private String fechaFin;

    @ManyToOne
    @JoinColumn(name = "id_trader")
    private Trader trader;
}