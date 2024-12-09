package com.tarefa.opombo.service;

import com.tarefa.opombo.auth.AuthenticationService;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.MensagemDTO;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.enums.SituacaoDenuncia;
import com.tarefa.opombo.model.repository.DenunciaRepository;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.model.seletor.MensagemSeletor;
import com.tarefa.opombo.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DenunciaRepository denunciaRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ImagemService imagemService;

    DenunciaService denunciaService = new DenunciaService();
    @Autowired
    private AuthenticationService authenticationService;

    public void salvarImagemMensagem(MultipartFile imagem, String idMensagem) throws OPomboException {

        Mensagem mensagemComImagem = mensagemRepository.
                findById(idMensagem)
                .orElseThrow(() -> new OPomboException("Mensagem não encontrada"));
        String imagemBase64 = imagemService.processarImagem(imagem);
        mensagemComImagem.setImagemEmBase64(imagemBase64);
        mensagemRepository.save(mensagemComImagem);
    }


    public List<Mensagem> buscarComSeletor(MensagemSeletor mensagemSeletor) {
        if (mensagemSeletor.temPaginacao()) {
            int numeroPagina = mensagemSeletor.getPagina();
            int tamanhoPagina = mensagemSeletor.getLimite();

            PageRequest pagina = PageRequest.of(numeroPagina - 1, tamanhoPagina, Sort.by(Sort.Direction.DESC, "dataHoraCriacao"));
            return mensagemRepository.findAll(mensagemSeletor, pagina).toList();
        }

        return mensagemRepository.findAll(mensagemSeletor, Sort.by(Sort.Direction.DESC, "dataHoraCriacao"));
    }

    public int contarPaginas(MensagemSeletor mensagemSeletor) {
        return (int) mensagemRepository.count(mensagemSeletor);
    }

    public List<Mensagem> buscarTodos() {
        return mensagemRepository.findAll(Sort.by(Sort.Direction.DESC, "dataHoraCriacao"));
    }

    public Mensagem buscarPorId(String id) throws OPomboException {
        return mensagemRepository.findById(id).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));
    }

    public List<Mensagem> buscarTodasMensagensPorIdUsuario(int idUsuario) throws OPomboException {

        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new OPomboException("Usuário não encontrado."));
        ;
        if (usuario != null) {
            return mensagemRepository.findByUsuario(usuario);
        }

        return List.of();
    }

    public List<Mensagem> buscarTodasMensagensAtivas() {
        return this.mensagemRepository.findAllNotBlockedAndNotDeleted();
    }

    public Mensagem salvar(Mensagem mensagem) throws OPomboException {

        Usuario usuario = usuarioRepository.findById(mensagem.getUsuario().getId()).orElseThrow(() -> new OPomboException("Usuário não encontrado."));

        mensagem.setUsuario(usuario);

        return mensagemRepository.save(mensagem);
    }

    public Mensagem alterar(Mensagem mensagemAlterada) throws OPomboException {
        authorizationService.verifiarCredenciaisUsuario(mensagemAlterada.getUsuario().getId());

        return mensagemRepository.save(mensagemAlterada);
    }

    public void excluir(String idMensagem) throws OPomboException {
        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));

        authorizationService.verifiarCredenciaisUsuario(mensagem.getUsuario().getId());

        mensagemRepository.deleteById(idMensagem);
    }

    public void marcarComoExcluida(String idMensagem) throws OPomboException {
        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Mensagem não encontrada"));

        authorizationService.verifiarCredenciaisUsuario(mensagem.getUsuario().getId());

        mensagem.setDeleted(true);

        mensagemRepository.save(mensagem);
    }

    public List<Usuario> buscarUsuariosQueCurtiramAMensagem(String idMensagem) throws OPomboException {
        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));
        return mensagem.getCurtidas();
    }

    public boolean darLike(String idMensagem) throws OPomboException {
        boolean curtiu = false;

        Usuario usuario = authenticationService.getUsuarioAutenticado();

       // Usuario usuario = usuarioRepository.findById(idUsuario).get();
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

    public String bloquearMensagem(String idMensagem) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        String resultado;
        Mensagem mensagem = mensagemRepository.findById(idMensagem).get();
        if (verificarSituacaoMensagem(idMensagem)) {
            mensagem.setBloqueado(true);
            resultado = "Mensagem bloqueada!";
        } else {
            throw new OPomboException("A mensagem não foi bloqueada");
        }

        mensagemRepository.save(mensagem);

        return resultado;
    }

    public boolean verificarSituacaoMensagem(String idMensagem) throws OPomboException {
        boolean temDenuncia = false;

        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));

        List<Denuncia> denuncias = mensagem.getDenuncias();

        if (denuncias.size() > 0) {
            temDenuncia = true;
        } else {
            throw new OPomboException("Para bloquear uma mensagem, ela precisar conter no mínimo uma denúncia.");
        }

        return temDenuncia;
    }

    public MensagemDTO gerarRelatorioMensagem(String idMensagem) throws OPomboException {
        authorizationService.verificarPerfilAcesso();
        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));

        Integer qtdCurtidas = buscarCurtidasMensagem(mensagem.getId());
        Integer qtdDenuncias = buscarDenunciasMensagem(mensagem.getId()).size();
        MensagemDTO mensagemDTO = Mensagem.toDTO(mensagem, qtdCurtidas, qtdDenuncias);

        return mensagemDTO;
    }

    public Integer buscarCurtidasMensagem(String idMensagem) throws OPomboException {
        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Publicação não encontrada."));

        return mensagem.getQuantidadeLikes();
    }

    public List<Denuncia> buscarDenunciasMensagem(String idMensagem) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        Mensagem mensagem = mensagemRepository.findById(idMensagem).orElseThrow(() -> new OPomboException("Publicação não encontrada."));

        return mensagem.getDenuncias();
    }

    public void verificarPerfilAcesso(int idUsuario) throws OPomboException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new OPomboException("Usuário não encontrado."));

        if (usuario.getPerfilAcesso() == PerfilAcesso.GERAL) {
            throw new OPomboException("Você não possui permissão para realizar essa ação.");
        }
    }
}
