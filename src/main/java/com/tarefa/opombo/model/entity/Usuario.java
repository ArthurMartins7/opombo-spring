package com.tarefa.opombo.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Set;

@Entity
@Table
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilAcesso perfilAcesso;

    @Column(nullable = false)
    private String nome;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @CPF
    @Column(nullable = false)
    private String cpf;

    @OneToMany(mappedBy = "usuario")
    private Set<Mensagem> mensagens;

    @ManyToMany(mappedBy = "curtidas")
    private Set<Mensagem> mensagensCurtidas;
}
