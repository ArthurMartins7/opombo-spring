package com.tarefa.opombo.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tarefa.opombo.model.dto.MensagemDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@Data
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "O conteúdo da mensagem não pode ser vazio ou apenas espaços em branco.")
    @Size(max = 300, message = "O conteúdo da mensagem deve conter no máximo 300 caracteres")
    @Size(min = 1, message = "O conteúdo da mensagem deve conter pelo menos 1 caractere, desconsiderando os espaços")
    private String texto;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraCriacao;

    @OneToMany(mappedBy = "mensagem")
    @JsonBackReference
    private List<Denuncia> denuncias;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(name = "mensagem_curtida", joinColumns = @JoinColumn(name = "id_usuario"), inverseJoinColumns = @JoinColumn(name = "id_mensagem"))
    private List<Usuario> curtidas;

    private Integer quantidadeLikes = 0;

    private boolean bloqueado = false;

    public static MensagemDTO toDTO(Mensagem mensagem, Integer quantidadeLikes, Integer quantidadeDenuncias) {
        return new MensagemDTO(
                mensagem.getId(),
                mensagem.getTexto(),
                quantidadeLikes,
                mensagem.getUsuario().getNome(),
                mensagem.getUsuario().getId(),
                quantidadeDenuncias
        );
    }


}
