package com.tarefa.opombo.model.repository;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.MotivoDenuncia;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.enums.SituacaoDenuncia;
import com.tarefa.opombo.service.DenunciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DenunciaRepositoryTest {

    @Mock
    private DenunciaRepository denunciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MensagemRepository mensagemRepository;

    @InjectMocks
    private DenunciaService denunciaService; // Supondo que exista um serviço para a lógica de denúncias

    private Usuario denunciante;
    private Mensagem mensagem;
    private Denuncia denuncia;

    @BeforeEach
    void configurar() {
        // Criando usuário (denunciante) mockado
        denunciante = new Usuario();
        denunciante.setNome("Teste Denunciante");
        denunciante.setSenha("senha123");
        denunciante.setCpf("32337132072");
        denunciante.setEmail("denunciante@example.com");
        denunciante.setPerfilAcesso(PerfilAcesso.ADMIN);

        mensagem = new Mensagem();
        mensagem.setId("123");
        mensagem.setTexto("Texto válido");
        mensagem.setUsuario(denunciante);

        denuncia = new Denuncia();
        denuncia.setMotivo(MotivoDenuncia.FRAUDE);
        denuncia.setMensagem(mensagem);
        denuncia.setDenunciante(denunciante);
        denuncia.setSituacao(SituacaoDenuncia.PENDENTE);

        when(usuarioRepository.save(denunciante)).thenReturn(denunciante);
        when(mensagemRepository.save(mensagem)).thenReturn(mensagem);
        when(denunciaRepository.save(denuncia)).thenReturn(denuncia);
        when(denunciaRepository.findById(denuncia.getId())).thenReturn(java.util.Optional.of(denuncia));
    }

    @Test
    void salvarDenunciaSucesso() throws OPomboException {
        Denuncia denunciaSalva = denunciaService.denunciar(denuncia);

        assertNotNull(denunciaSalva.getId());
        assertEquals(MotivoDenuncia.FRAUDE, denunciaSalva.getMotivo());
        assertEquals(SituacaoDenuncia.PENDENTE, denunciaSalva.getSituacao());
    }

    @Test
    void buscarPorId_Sucesso() {
        Denuncia denunciaEncontrada = denunciaRepository.findById(denuncia.getId()).orElse(null);

        assertNotNull(denunciaEncontrada);
        assertEquals(denuncia.getId(), denunciaEncontrada.getId());
        assertEquals(denuncia.getMotivo(), denunciaEncontrada.getMotivo());
    }

    @Test
    void buscarPorId_Falha_NaoEncontrado() {
        when(denunciaRepository.findById(999)).thenReturn(java.util.Optional.empty());

        Denuncia denunciaEncontrada = denunciaRepository.findById(999).orElse(null);

        assertNull(denunciaEncontrada);
    }

    @Test
    void excluirPorId_Sucesso() {
        doNothing().when(denunciaRepository).deleteById(denuncia.getId());

        denunciaRepository.deleteById(denuncia.getId());
        Denuncia denunciaExcluida = denunciaRepository.findById(denuncia.getId()).orElse(null);

        assertNull(denunciaExcluida);
    }

    @Test
    void excluirPorId_Falha() {
        doNothing().when(denunciaRepository).deleteById(999);

        assertDoesNotThrow(() -> denunciaRepository.deleteById(999));

        verify(denunciaRepository, times(1)).deleteById(999);
    }

}
