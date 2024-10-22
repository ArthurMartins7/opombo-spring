package com.tarefa.opombo.factories;

import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;

public class UsuarioFactory {

    public static Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setPerfilAcesso(PerfilAcesso.ADMIN);
        usuario.setNome("Usuario");
        usuario.setEmail("usuario@example.com");
        usuario.setSenha("usuario123");
        usuario.setCpf("14826965923");

        return usuario;
    }

}
