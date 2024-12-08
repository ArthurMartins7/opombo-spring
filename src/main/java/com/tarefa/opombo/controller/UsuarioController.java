package com.tarefa.opombo.controller;

import com.tarefa.opombo.auth.AuthenticationService;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.UsuarioEditadoDTO;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.seletor.UsuarioSeletor;
import com.tarefa.opombo.security.AuthorizationService;
import com.tarefa.opombo.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/usuario")
@MultipartConfig(fileSizeThreshold = 10485760) // 10MB
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private AuthenticationService authenticationService;



    @Operation(
            summary = "Upload de Imagem de perfil para Usuario",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Arquivo de imagem a ser enviado",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            description = "Realiza o upload de uma imagem associada a um usuario específico."
    )
    @PostMapping("/{idUsuario}/upload")
    public void fazerUploadImagemUsuario(@RequestParam("imagem") MultipartFile imagem,
                                 @PathVariable Integer idUsuario)
            throws OPomboException, IOException {

        if (imagem == null) {
            throw new OPomboException("Arquivo inválido");
        }

        Usuario usuarioAutenticado = authenticationService.getUsuarioAutenticado();
        if (usuarioAutenticado == null) {
            throw new OPomboException("Usuário não encontrado");
        }

//        if (usuarioAutenticado.getPerfilAcesso() == PerfilAcesso.GERAL) {
//            throw new OPomboException("Usuário sem permissão de acesso");
//        }

        usuarioService.salvarImagemUsuario(imagem, idUsuario);
    }

    @Operation(summary = "Buscar usuários com seletor")
    @PostMapping("/filtro")
    public List<Usuario> buscarComSeletor(@RequestBody UsuarioSeletor usuarioSeletor) throws OPomboException {
        return usuarioService.buscarComSeletor(usuarioSeletor);
    }

    @Operation(summary = "Buscar todos os usuários",
            description = "Retorna uma lista de todos os usuários cadastrados no sistema.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
            })

    @GetMapping
    public List<Usuario> buscarTodos() throws OPomboException {
        authorizationService.verificarPerfilAcesso();
        return usuarioService.buscarTodos();
    }

    @Operation(summary = "Pesquisar usuário por ID",
            description = "Busca um usuário específico pelo seu ID.")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable int id) throws OPomboException {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Salvar novo usuário",
            description = "Adiciona um novo usuário ao sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario criada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Usuario.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"Erro de validação: campo X é obrigatório\", \"status\": 400}")))})


    @PostMapping("/public")
    public ResponseEntity<Usuario> salvar(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioSalvo = usuarioService.salvar(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        } catch (OPomboException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Alterar usuário existente", description = "Atualiza os dados de um usuário existente.")
    @PutMapping("/{idUsuario}")
    public ResponseEntity<Usuario> alterar(@Valid @PathVariable int idUsuario, @RequestBody UsuarioEditadoDTO usuarioEditado) throws OPomboException {
        return ResponseEntity.ok(usuarioService.alterar(idUsuario, usuarioEditado));
    }

    @Operation(summary = "Excluir usuário por ID", description = "Remove um usuário específico pelo seu ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) throws OPomboException {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

}
