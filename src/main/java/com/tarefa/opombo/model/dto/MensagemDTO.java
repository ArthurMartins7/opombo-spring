package com.tarefa.opombo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MensagemDTO {

    private String id;
    private String texto = "**Mensagem Bloqueada**";
    private boolean bloqueado;
    private Integer idUsuario;
    private String nomeUsuario;
    private Integer totalDenuncias;
    private Integer qtdCurtidas;

}
