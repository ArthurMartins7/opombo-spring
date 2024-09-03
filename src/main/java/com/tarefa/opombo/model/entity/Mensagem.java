package com.tarefa.opombo.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table
@Data
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String texto;

    private LocalDateTime dataHoraCriacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToMany
    private Set<Usuario> like;

    @ManyToMany
    @JoinTable(name = "mensagem_curtida", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_mensagem"))
    private Set<Mensagem> curtidas;


}
