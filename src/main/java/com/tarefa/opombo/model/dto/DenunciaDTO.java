package com.tarefa.opombo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DenunciaDTO {

    private String idMensagem;
    private Integer qtdDenuncia;
    private Integer qtdDenunciasPendentes;
    private Integer qtdDenunciasAnalisadas;
}
