package com.tarefa.opombo.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UsuarioEditadoDTO {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilAcesso perfilAcesso;

    @NotBlank(message = "O nome não pode ser vazio ou apenas espaços em branco.")
    private String nome;

    @Email
    @NotBlank(message = "O nome não pode ser vazio ou apenas espaços em branco.")
    private String email;


    @NotBlank(message = "A senha não pode ser vazio ou apenas espaços em branco.")
    @Column(length = 4000)
    private String senha;

    @CPF
    @NotBlank(message = "O CPF não pode ser vazio ou apenas espaços em branco.")
    @Column(unique = true, nullable = false)
    private String cpf;


}
