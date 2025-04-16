package com.edu.unbosque.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "trader")
@Data
public class Trader extends Usuario {

    private Integer experiencia;
}