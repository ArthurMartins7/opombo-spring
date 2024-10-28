package com.tarefa.opombo.security;

import com.tarefa.opombo.auth.AuthenticationService;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.model.seletor.UsuarioSeletor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void verificarPerfilAcesso() throws OPomboException {
        Usuario usuarioAutenticado = authenticationService.getUsuarioAutenticado();

        if (usuarioAutenticado.getPerfilAcesso() != PerfilAcesso.ADMIN) {
            throw new OPomboException("Usuário sem permissão de acesso!");
        }
    }

    public void verifiarCredenciaisUsuario(int idUsuarioURL) throws OPomboException {
        Usuario usuarioURL = usuarioRepository.findById(idUsuarioURL).orElseThrow(() -> new OPomboException("Usuário não encontrado"));

        Usuario usuarioAutenticado = authenticationService.getUsuarioAutenticado();

        if (usuarioAutenticado.getId() != usuarioURL.getId()) {
            throw new OPomboException("Somente o portador da conta pode executar essa ação!");
        }
    }
}
