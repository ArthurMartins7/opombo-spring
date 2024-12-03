package com.tarefa.opombo.controller;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.DenunciaDTO;
import com.tarefa.opombo.model.dto.MensagemDTO;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.seletor.DenunciaSeletor;
import com.tarefa.opombo.model.seletor.MensagemSeletor;
import com.tarefa.opombo.service.DenunciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/denuncia")
public class DenunciaController {

    @Autowired
    DenunciaService denunciaService;

    @Operation(summary = "Buscar todas as denúncias")
    @GetMapping
    public List<Denuncia> buscarTodas() throws OPomboException {
        return denunciaService.buscarTodas();
    }

    @Operation(summary = "Buscar denúncia por id")
    @GetMapping("/{idDenuncia}")
    public Denuncia buscarPorId(@PathVariable int idDenuncia) throws OPomboException {
        return denunciaService.buscarPorId(idDenuncia);
    }

    @Operation(summary = "Relatório", description = "Busca um DTO (relatório de denúncias) por id da mensagem")
    @GetMapping("/dto/{idMensagem}")
    public DenunciaDTO gerarRelatorioMensagem(@RequestParam String idMensagem) throws OPomboException {
        return denunciaService.gerarRelatorioDenunciasPorIdMensagem(idMensagem);
    }

    @Operation(summary = "Denunciar")
    @PostMapping
    public Denuncia denunciar(@RequestBody @Valid Denuncia denuncia) throws OPomboException {
        return denunciaService.denunciar(denuncia);
    }

    @Operation(summary = "Buscar denuncias com seletor")
    @PostMapping("/filtro")
    public List<Denuncia> buscarComSeletor(@RequestBody DenunciaSeletor denunciaSeletor) throws OPomboException {
        return denunciaService.buscarComSeletor(denunciaSeletor);
    }


    @Operation(summary = "Alterar denúncia")
    @PutMapping
    public Denuncia alterar(@RequestBody Denuncia denuncia) throws OPomboException {
        return denunciaService.alterar(denuncia);
    }

    @Operation(summary = "Excluir denúncia")
    @DeleteMapping("/{id}")
    public void excluir (@PathVariable int id) throws OPomboException {
        denunciaService.excluir(id);
    }

    @Operation(summary = "Aceitar denúncia", description = "Aceita a denúncia de um usuario")
    @PostMapping("/aceitarDenuncia/{idDenuncia}")
    public boolean aceitarDenuncia(@PathVariable int idDenuncia) throws OPomboException {
        return denunciaService.aceitarDenuncia(idDenuncia);
    }

    @Operation(summary = "Rejeitar denúncia", description = "Rejeita a denúncia de um usuario")
    @PostMapping("/rejeitarDenuncia/{idDenuncia}")
    public boolean rejeitarDenuncia(@PathVariable int idDenuncia) throws OPomboException {
        return denunciaService.rejeitarDenuncia(idDenuncia);
    }

}
