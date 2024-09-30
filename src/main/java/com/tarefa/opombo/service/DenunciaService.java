package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.enums.SituacaoDenuncia;
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

    public Denuncia denunciar(Denuncia denuncia) throws OPomboException {
        denuncia.setSituacao(SituacaoDenuncia.PENDENTE);

        return denunciaRepository.save(denuncia);
    }

    public Denuncia alterar(Denuncia denunciaAlterada) throws OPomboException {
        return denunciaRepository.save(denunciaAlterada);
    }

    public void excluir(Integer idDenuncia) throws OPomboException {
        denunciaRepository.deleteById(idDenuncia);
    }

    public boolean analisarDenuncia(int idUsuario, int idDenuncia) throws OPomboException {

        verificarPerfilAcesso(idUsuario);
        Denuncia denuncia = denunciaRepository.findById(idDenuncia).orElseThrow(() -> new OPomboException("Denúncia não encontrada"));

        boolean analisada = false;

        List<Denuncia> denuncias = denunciaRepository.findAll();
        denuncias.add(denuncia);

        if (denuncia.getSituacao().equals(SituacaoDenuncia.PENDENTE)) {
            for (Denuncia d : denuncias) {
                d.setSituacao(SituacaoDenuncia.ANALISADA);
            }
            analisada = true;
            denunciaRepository.save(denuncia);
        } else {
            throw new OPomboException("A denúncia já foi analisada.");
        }

        return analisada;
    }


    public void verificarPerfilAcesso(int idUsuario) throws OPomboException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new OPomboException("Usuário não encontrado."));

        if (usuario.getPerfilAcesso() == PerfilAcesso.GERAL) {
            throw new OPomboException("Você não possui permissão para realizar essa ação.");
        }
    }


}
