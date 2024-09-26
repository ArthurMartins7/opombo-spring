package com.tarefa.opombo.controller;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.service.DenunciaService;
import io.swagger.v3.oas.annotations.Operation;
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
    public List<Denuncia> buscarTodas(@RequestParam  int idUsuario) throws OPomboException {
        return denunciaService.buscarTodas(idUsuario);
    }

    @Operation(summary = "Buscar denúncia por id")
    @GetMapping("/{idDenuncia}")
    public Denuncia buscarPorId(@RequestParam int idDenuncia, int idUsuario) throws OPomboException {
        return denunciaService.buscarPorId(idDenuncia, idUsuario);
    }

    @Operation(summary = "Denunciar")
    @PostMapping
    public Denuncia denunciar(@RequestBody @Valid Denuncia denuncia) throws OPomboException {
        return denunciaService.denunciar(denuncia);
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

}
