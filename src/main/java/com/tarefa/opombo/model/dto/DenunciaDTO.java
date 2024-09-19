package com.tarefa.opombo.model.dto;

import lombok.Data;

@Data
public class DenunciaDTO {

    private String idMensagem;
    private Integer qtdDenunciasMensagem;
    private Integer qtdDenunciasPendentes;
    private Integer qtdDenunciasAnalisadas;
}
