package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(int id) throws OPomboException {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new OPomboException("Carta não encontrada."));
    }

    public Usuario salvar(Usuario usuario) throws OPomboException {
        return usuarioRepository.save(usuario);
    }

    public Usuario alterar(Usuario usuarioAlterado) throws OPomboException {
        return usuarioRepository.save(usuarioAlterado);
    }

    public void excluir(int id) {
        usuarioRepository.deleteById(id);
    }
}
