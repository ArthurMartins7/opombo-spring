package com.tarefa.opombo.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;
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

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "usuario")
    private Set<Mensagem> mensagens;

    @ManyToMany(mappedBy = "curtidas", fetch = FetchType.EAGER)
    private Set<Mensagem> mensagensCurtidas;

}
