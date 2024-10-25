package com.tarefa.opombo.security;

import com.tarefa.opombo.auth.AuthenticationService;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private AuthenticationService authenticationService;

    public void verificarPerfilAcesso() throws OPomboException {
        Usuario user = authenticationService.getUsuarioAutenticado();

        if (user.getPerfilAcesso() != PerfilAcesso.ADMIN) {
            throw new OPomboException("Usuário sem permissão de acesso!");
        }
    }
}
