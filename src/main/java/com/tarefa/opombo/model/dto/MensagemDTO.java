package com.tarefa.opombo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensagemDTO {

    private String id;
    private String texto = "**Mensagem Bloqueada**";
    private Integer qtdCurtidas;
    private String nomeUsuario;
    private Integer idUsuario;
    private Integer totalDenuncias;

}
