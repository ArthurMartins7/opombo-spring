package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.DenunciaDTO;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.enums.SituacaoDenuncia;
import com.tarefa.opombo.model.repository.DenunciaRepository;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DenunciaService {

    @Autowired
    DenunciaRepository denunciaRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthorizationService authorizationService;

    public List<Denuncia> buscarTodas() throws OPomboException {
        authorizationService.verificarPerfilAcesso();
        return denunciaRepository.findAll();
    }

    public Denuncia buscarPorId(int idDenuncia) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        return denunciaRepository.findById(idDenuncia).orElseThrow(() -> new OPomboException("Denúncia não encontrada."));
    }

    public Denuncia denunciar(Denuncia denuncia) throws OPomboException {

        List<Denuncia> denuncias = denunciaRepository.findAll();

        for (Denuncia d : denuncias) {
            if (!(d.getDenunciante().getId() == denuncia.getDenunciante().getId())) {
                denuncia.setSituacao(SituacaoDenuncia.PENDENTE);
            } else {
                throw new OPomboException("Voc^r só pode fazer uma denúncia por mensagem!");
            }
        }
        return denunciaRepository.save(denuncia);
    }

    public Denuncia alterar(Denuncia denunciaAlterada) throws OPomboException {
        authorizationService.verifiarCredenciaisUsuario(denunciaAlterada.getDenunciante().getId());

        return denunciaRepository.save(denunciaAlterada);
    }

    public void excluir(Integer idDenuncia) throws OPomboException {
        Denuncia denuncia = denunciaRepository.findById(idDenuncia).orElseThrow(() -> new OPomboException("Denúncia não encontrada!"));

        authorizationService.verifiarCredenciaisUsuario(denuncia.getDenunciante().getId());

        denunciaRepository.deleteById(idDenuncia);
    }

    //DTO
    public DenunciaDTO gerarRelatorioDenunciasPorIdMensagem(String idMensagem) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Mensagem não encontrada"));

        List<Denuncia> denuncias = mensagem.getDenuncias();
        List<Denuncia> denunciasPendentes = new ArrayList<>();
        List<Denuncia> denunciasAnalisadas = new ArrayList<>();

        for (Denuncia denuncia : denuncias) {
            if (denuncia.getSituacao().equals(SituacaoDenuncia.PENDENTE)) {
                denunciasPendentes.add(denuncia);
            } else if (denuncia.getSituacao().equals(SituacaoDenuncia.ANALISADA)) {
                denunciasAnalisadas.add(denuncia);
            }
        }

        DenunciaDTO denunciaDTO = Denuncia.toDTO(idMensagem, denuncias.size(), denunciasPendentes.size(), denunciasAnalisadas.size());
        return denunciaDTO;
    }

    public boolean analisarDenuncia(int idDenuncia) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

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
