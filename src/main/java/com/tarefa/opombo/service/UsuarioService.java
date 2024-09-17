package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.model.seletor.UsuarioSeletor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    public List<Usuario> buscarComSeletor(UsuarioSeletor usuarioSeletor) {
        if (usuarioSeletor.temPaginacao()) {
            int numeroPagina = usuarioSeletor.getPagina();
            int tamanhoPagina = usuarioSeletor.getLimite();

            PageRequest pagina = PageRequest.of(numeroPagina - 1, tamanhoPagina, Sort.by(Sort.Direction.DESC, "criadoEm"));
            return usuarioRepository.findAll(usuarioSeletor, pagina).toList();
        }

        return usuarioRepository.findAll(usuarioSeletor, Sort.by(Sort.Direction.DESC, "criadoEm"));
    }

    public int contarPaginas(UsuarioSeletor usuarioSeletor) {
        return (int) usuarioRepository.count(usuarioSeletor);
    }

    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.DESC, "criadoEm"));
    }

    public Usuario buscarPorId(int id) throws OPomboException {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new OPomboException("Usuário não encontrado."));
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
