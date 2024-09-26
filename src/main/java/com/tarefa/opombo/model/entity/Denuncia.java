package com.tarefa.opombo.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tarefa.opombo.model.enums.MotivoDenuncia;
import com.tarefa.opombo.model.enums.SituacaoDenuncia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotivoDenuncia motivo;

    @Enumerated(EnumType.STRING)
    private SituacaoDenuncia situacao;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraCriacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario denunciante;

    @ManyToOne
    @JoinColumn(name = "id_mensagem")
    private Mensagem mensagem;


}
