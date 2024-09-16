package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.repository.DenunciaRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DenunciaService {

    @Autowired
    DenunciaRepository denunciaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Denuncia> buscarTodas(int idUsuario) throws OPomboException {
        verificarPerfilAcesso(idUsuario);
        return denunciaRepository.findAll();
    }

    public Denuncia buscarPorId(int idDenuncia, int idUsuario) throws OPomboException {
        verificarPerfilAcesso(idUsuario);
        return denunciaRepository.findById(idDenuncia).orElseThrow(() -> new OPomboException("Denúncia não encontrada."));
    }

    public Denuncia salvar(Denuncia denuncia) {
        return denunciaRepository.save(denuncia);
    }

    public Denuncia alterar(Denuncia denunciaAlterada) throws OPomboException {
        return denunciaRepository.save(denunciaAlterada);
    }

    public void excluir(Integer idDenuncia) throws OPomboException {
        denunciaRepository.deleteById(idDenuncia);
    }

    public void verificarPerfilAcesso(int idUsuario) throws OPomboException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new OPomboException("Usuário não encontrado."));

        if(usuario.getPerfilAcesso() == PerfilAcesso.GERAL) {
            throw new OPomboException("Você não possui permissão para realizar essa ação.");
        }
    }
}
