package com.edu.unbosque.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "comisionista")
@Data
public class Comisionista extends Usuario {


    private Integer experiencia;
}
