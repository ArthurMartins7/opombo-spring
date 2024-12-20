package com.tarefa.opombo.controller;

import com.tarefa.opombo.auth.AuthenticationService;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Método de login padronizado -> Basic Auth
     * <p>
     * O parâmetro Authentication já encapsula login (username) e senha (password)
     * Basic <Base64 encoded username and password>
     *
     * @param authentication
     * @return o JWT gerado
     */
    @PostMapping("authenticate")
    public String authenticate(Authentication authentication) throws OPomboException {
        return authenticationService.authenticate(authentication);
    }

    @PostMapping("/novo-usuario")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Usuario registrarUsuario(@RequestBody Usuario novoUsuario) throws OPomboException {

        String senhaCifrada = passwordEncoder.encode(novoUsuario.getSenha());

        novoUsuario.setSenha(senhaCifrada);

        if(novoUsuario.getPerfilAcesso() == null || novoUsuario.getPerfilAcesso().toString().isEmpty()) {
            novoUsuario.setPerfilAcesso(PerfilAcesso.GERAL);
        }
        usuarioService.salvar(novoUsuario);

        return novoUsuario;
    }

}