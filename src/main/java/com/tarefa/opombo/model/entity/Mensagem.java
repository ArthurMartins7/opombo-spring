package com.tarefa.opombo.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
}
