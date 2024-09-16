package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Mensagem> buscarTodos() {
        return mensagemRepository.findAll();
    }

    public Mensagem buscarPorId(String id) throws OPomboException {
        return mensagemRepository.findById(id).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));
    }

    public Mensagem salvar(Mensagem mensagem) throws OPomboException {
        return mensagemRepository.save(mensagem);
    }

    public Mensagem alterar(Mensagem mensagemAlterada) throws OPomboException {
        return mensagemRepository.save(mensagemAlterada);
    }

    public void excluir(String id) {
        mensagemRepository.deleteById(id);
    }

    public List<Usuario> buscarUsuariosQueCurtiramAMensagem(String idMensagem) throws OPomboException {
        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));
        return mensagem.getCurtidas();
    }

    public boolean darLike(Integer idUsuario, String idMensagem) {
        boolean curtiu = false;

        Usuario usuario = usuarioRepository.findById(idUsuario).get();
        Mensagem mensagem = mensagemRepository.findById(idMensagem).get();

        int totalLikesAtual = mensagem.getQuantidadeLikes();
        if (mensagem.getCurtidas().contains(usuario)) {
            mensagem.getCurtidas().remove(usuario);
            mensagem.setQuantidadeLikes(totalLikesAtual - 1);
        } else {
            mensagem.getCurtidas().add(usuario);
            mensagem.setQuantidadeLikes(totalLikesAtual + 1);
            curtiu = true;
        }

        this.mensagemRepository.save(mensagem);

        return curtiu;
    }

    public String bloquearMensagem(String idMensagem, int idUsuario) throws OPomboException {
        verificarPerfilAcesso(idUsuario);

        String resultado;

        Mensagem mensagem = mensagemRepository.findById(idMensagem).get();
        if (mensagem != null) {
            mensagem.setBloqueado(true);
            mensagem.setTexto("**Esse texto está bloqueado.**");
            resultado = "Mensagem bloqueada!";
        } else {
            throw new OPomboException("A mensagem não foi bloqueada");
        }

        mensagemRepository.save(mensagem);

        return resultado;
    }

    public void verificarPerfilAcesso(int idUsuario) throws OPomboException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new OPomboException("Usuário não encontrado."));

        if (usuario.getPerfilAcesso() == PerfilAcesso.GERAL) {
            throw new OPomboException("Você não possui permissão para realizar essa ação.");
        }
    }
}
