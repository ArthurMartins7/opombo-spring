package com.tarefa.opombo.controller;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.service.MensagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/mensagem")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;

    @Operation(summary = "Buscar todos os usuários",
            description = "Retorna uma lista de todas as mensagens cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de mensagens retornada com sucesso")
            })

    @GetMapping
    public List<Mensagem> buscarTodos() {
        return mensagemService.buscarTodos();
    }

    @Operation(summary = "Pesquisar mensagem por ID",
            description = "Busca uma mensagem específica pelo seu ID.")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Mensagem> buscarPorId(@PathVariable String id) throws OPomboException {
        Mensagem mensagem = mensagemService.buscarPorId(id);
        return ResponseEntity.ok(mensagem);
    }

    @Operation(summary = "Salvar nova mensagem",
            description = "Adiciona uma nova mensagem ao sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Mensagem criada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Mensagem.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"Erro de validação: campo X é obrigatório\", \"status\": 400}")))})


    @PostMapping
    public ResponseEntity<Mensagem> salvar(@Valid @RequestBody Mensagem mensagem) {
        try {
            Mensagem mensagemSalva = mensagemService.salvar(mensagem);
            return new ResponseEntity(mensagemSalva, HttpStatus.CREATED);
        } catch (OPomboException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Alterar mensagem existente", description = "Atualiza os dados de uma mensagem existente.")
    @PutMapping
    public ResponseEntity<Mensagem> alterar(@Valid @RequestBody Mensagem mensagemEditada) throws OPomboException {
        return ResponseEntity.ok(mensagemService
                .alterar(mensagemEditada));
    }

    @Operation(summary = "Excluir mensagem por ID", description = "Remove um mensagem específico pelo seu ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        mensagemService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar usuários que curtiram a mensagem")
    @GetMapping("/curitdas")
    public List<Usuario> buscarUsuariosQueCurtiramAMensagem(@RequestParam String IdMensagem) throws OPomboException {
        return  mensagemService.buscarUsuariosQueCurtiramAMensagem(IdMensagem);
    }

    @Operation(summary = "Curtir a mensagem", description = "Curte a mensagem ou descurte se o usuário já curtiu.")
    @PostMapping("/{idUsuario}/{idMensagem}")
    public boolean darLike(@PathVariable Integer idUsuario, @PathVariable String idMensagem) throws OPomboException {
        return mensagemService.darLike(idUsuario, idMensagem);
    }

    @Operation(summary = "Bloquear mensagem", description = "Bloqueia a mensagem de um usuário")
    @PostMapping("/{idMensagem}")
    public String bloquearMensagem(@PathVariable String idMensagem, int idUsuario) throws OPomboException {
        return mensagemService.bloquearMensagem(idMensagem, idUsuario);
    }

}
