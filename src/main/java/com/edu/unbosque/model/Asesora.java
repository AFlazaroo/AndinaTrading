package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "asesora")
@Data
public class Asesora {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_trader")
    private Trader trader;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_comisionista")
    private Comisionista comisionista;
}

