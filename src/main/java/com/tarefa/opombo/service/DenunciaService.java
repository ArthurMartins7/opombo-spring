package com.tarefa.opombo.service;

import com.tarefa.opombo.auth.AuthenticationService;
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
import com.tarefa.opombo.model.seletor.DenunciaSeletor;
import com.tarefa.opombo.model.seletor.MensagemSeletor;
import com.tarefa.opombo.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    private AuthenticationService authenticationService;

    public List<Denuncia> buscarTodas() throws OPomboException {
        authorizationService.verificarPerfilAcesso();
        return denunciaRepository.findAll();
    }

    public Denuncia buscarPorId(int idDenuncia) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        return denunciaRepository.findById(idDenuncia).orElseThrow(() -> new OPomboException("Denúncia não encontrada."));
    }

    public List<Denuncia> buscarComSeletor(DenunciaSeletor denunciaSeletor) {
        if (denunciaSeletor.temPaginacao()) {
            int numeroPagina = denunciaSeletor.getPagina();
            int tamanhoPagina = denunciaSeletor.getLimite();

            PageRequest pagina = PageRequest.of(numeroPagina - 1, tamanhoPagina, Sort.by(Sort.Direction.DESC, "dataHoraCriacao"));
            return denunciaRepository.findAll(denunciaSeletor, pagina).toList();
        }

        return denunciaRepository.findAll(denunciaSeletor, Sort.by(Sort.Direction.DESC, "dataHoraCriacao"));
    }

    public Denuncia denunciar(Denuncia denuncia) throws OPomboException {
        this.authorizationService.verificarPerfilAcesso();

        this.authorizationService.verifiarCredenciaisUsuario(denuncia.getDenunciante().getId());

        denuncia.setDenunciante(authenticationService.getUsuarioAutenticado());

        this.verificarSeUsuarioJaDenunciouAMensagem(denuncia);

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
        List<Denuncia> denunciasBloqueadas = new ArrayList<>();
        List<Denuncia> denunciasRejeitadas = new ArrayList<>();

        for (Denuncia denuncia : denuncias) {
            if (denuncia.getSituacao().equals(SituacaoDenuncia.PENDENTE)) {
                denunciasPendentes.add(denuncia);
            } else if (denuncia.getSituacao().equals(SituacaoDenuncia.BLOQUEADA)) {
                denunciasBloqueadas.add(denuncia);
            } else {
                denunciasRejeitadas.add(denuncia);
            }
        }

        DenunciaDTO denunciaDTO = Denuncia.toDTO(idMensagem, denuncias.size(), denunciasPendentes.size(), denunciasBloqueadas.size(), denunciasRejeitadas.size());
        return denunciaDTO;
    }

    public boolean aceitarDenuncia(int idDenuncia) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        Denuncia denuncia = denunciaRepository.findById(idDenuncia).orElseThrow(() -> new OPomboException("Denúncia não encontrada"));

        boolean aceita = false;

        if (denuncia.getSituacao().equals(SituacaoDenuncia.PENDENTE)) {
            denuncia.setSituacao((SituacaoDenuncia.BLOQUEADA));
            aceita = true;
            denunciaRepository.save(denuncia);
        } else {
            throw new OPomboException("A denúncia já foi analisada.");
        }

        return aceita;
    }

    public boolean rejeitarDenuncia(int idDenuncia) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        Denuncia denuncia = denunciaRepository.findById(idDenuncia).orElseThrow(() -> new OPomboException("Denúncia não encontrada"));

        boolean rejeitada = false;

        if (denuncia.getSituacao().equals(SituacaoDenuncia.PENDENTE)) {
            denuncia.setSituacao(SituacaoDenuncia.REJEITADA);
            rejeitada = true;
            denunciaRepository.save(denuncia);
        } else {
            throw new OPomboException("A denúncia já foi analisada.");
        }
        return rejeitada;
    }


    public void verificarPerfilAcesso(int idUsuario) throws OPomboException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new OPomboException("Usuário não encontrado."));

        if (usuario.getPerfilAcesso() == PerfilAcesso.GERAL) {
            throw new OPomboException("Você não possui permissão para realizar essa ação.");
        }
    }

    public void verificarSeUsuarioJaDenunciouAMensagem(Denuncia denuncia) throws OPomboException {
        boolean jaDenunciou = denunciaRepository.existsByMensagemIdAndDenuncianteId(denuncia.getMensagem().getId(), denuncia.getDenunciante().getId());

        if (jaDenunciou) {
            throw new OPomboException("Você só pode denunciar a mensagem uma vez.");
        }
    }

}
