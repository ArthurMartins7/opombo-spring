package com.tarefa.opombo.controller;

import com.tarefa.opombo.auth.AuthenticationService;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.MensagemDTO;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.seletor.MensagemSeletor;
import com.tarefa.opombo.model.seletor.UsuarioSeletor;
import com.tarefa.opombo.security.AuthorizationService;
import com.tarefa.opombo.service.MensagemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/api/mensagens")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private AuthenticationService authenticationService;

    @Operation(
            summary = "Upload de Imagem para mensagem",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Arquivo de imagem a ser enviado",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            description = "Realiza o upload de uma imagem associada a uma mensagem específica."
    )
    @PostMapping("/{idMensagem}/upload")
    public void fazerUploadMensagem(@RequestParam("imagem") MultipartFile imagem,
                                @PathVariable String idMensagem)
            throws OPomboException, IOException {

        if(imagem == null) {
            throw new OPomboException("Arquivo inválido");
        }

        Usuario usuarioAutenticado = authenticationService.getUsuarioAutenticado();
        if(usuarioAutenticado == null) {
            throw new OPomboException("Usuário não encontrado");
        }

        mensagemService.salvarImagemMensagem(imagem, idMensagem);
    }

    @Operation(summary = "Buscar mensagens com seletor")
    @PostMapping("/filtro")
    public List<Mensagem> buscarComSeletor(@RequestBody MensagemSeletor mensagemSeletor) throws OPomboException {
        return mensagemService.buscarComSeletor(mensagemSeletor);
    }

    @Operation(summary = "Buscar todas as mensagens",
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

    @Operation(summary = "Busca todas as mensagens de um usuário especfico pelo seu ID")
    @GetMapping("/user/{idUsuario}")
    public List<Mensagem> buscarTodasMensagensPorIdUsuario(int idUsuario) throws OPomboException {
        return mensagemService.buscarTodasMensagensPorIdUsuario(idUsuario);
    }


    @Operation(summary = "Relatório", description = "Busca um DTO (relatório de mensagem)")
    @GetMapping("/dto")
    public MensagemDTO gerarRelatorioMensagem(@RequestParam String idMensagem) throws OPomboException {
        return mensagemService.gerarRelatorioMensagem(idMensagem);
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
    public ResponseEntity<Void> excluir(@PathVariable String id) throws OPomboException {
        mensagemService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar usuários que curtiram a mensagem")
    @GetMapping("/curtidas")
    public List<Usuario> buscarUsuariosQueCurtiramAMensagem(@RequestParam String IdMensagem) throws OPomboException {
        return  mensagemService.buscarUsuariosQueCurtiramAMensagem(IdMensagem);
    }

    @Operation(summary = "Curtir a mensagem", description = "Curte a mensagem ou descurte se o usuário já curtiu.")
    @PostMapping("/{idUsuario}/{idMensagem}")
    public boolean darLike(@PathVariable Integer idUsuario, @PathVariable String idMensagem) throws OPomboException {
        return mensagemService.darLike(idUsuario, idMensagem);
    }

    @Operation(summary = "Bloquear mensagem", description = "Bloqueia a mensagem de um usuário")
    @GetMapping("/bloquear/{idMensagem}")
    public ResponseEntity bloquearMensagem(@PathVariable String idMensagem) throws OPomboException {
        mensagemService.bloquearMensagem(idMensagem);
        return ResponseEntity.ok().build();
    }
}
