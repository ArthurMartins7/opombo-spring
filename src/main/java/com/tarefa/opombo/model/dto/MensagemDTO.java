package com.tarefa.opombo.model.dto;

import lombok.Data;

@Data
public class MensagemDTO {

    private Integer qtdCurtidas;
    private String texto = "**Mensagem Bloqueada**";
    private String nomeUsuario;
    private Integer totalDenuncias;
    private Integer idUsuario;

}
