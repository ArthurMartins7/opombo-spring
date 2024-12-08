package com.tarefa.opombo.service;

import com.tarefa.opombo.auth.AuthenticationService;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.DenunciaDTO;
import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.MotivoDenuncia;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.enums.SituacaoDenuncia;
import com.tarefa.opombo.model.repository.DenunciaRepository;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.security.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

public class DenunciaServiceTest {

    @Mock
    private DenunciaRepository denunciaRepository;

    @Mock
    private MensagemRepository mensagemRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private DenunciaService denunciaService;

    private Denuncia denuncia;
    private Mensagem mensagem;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("arthur");
        usuario.setCpf("13663280942");
        usuario.setEmail("artrs@gmail.com");
        usuario.setPerfilAcesso(PerfilAcesso.ADMIN);

        mensagem = new Mensagem();
        mensagem.setId("1");
        mensagem.setTexto("tzatusza");


        denuncia = new Denuncia();
        denuncia.setId(1);
        denuncia.setMotivo(MotivoDenuncia.FRAUDE);
        denuncia.setSituacao(SituacaoDenuncia.PENDENTE);
        denuncia.setDenunciante(usuario);
        denuncia.setMensagem(mensagem);
    }

    @Test
    public void testDenunciar_Sucesso() throws OPomboException {
        when(authenticationService.getUsuarioAutenticado()).thenReturn(usuario);
        when(denunciaRepository.existsByMensagemIdAndDenuncianteId(anyString(), anyInt())).thenReturn(false);
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        Denuncia resultado = denunciaService.denunciar(denuncia);

        assertNotNull(resultado);
        assertEquals(denuncia.getId(), resultado.getId());
        verify(denunciaRepository, times(1)).save(denuncia);
    }

    @Test
    public void testDenunciar_Falha_UsuarioJaDenunciou() throws OPomboException {
        when(authenticationService.getUsuarioAutenticado()).thenReturn(usuario);
        when(denunciaRepository.existsByMensagemIdAndDenuncianteId(anyString(), anyInt())).thenReturn(true);

        OPomboException exception = assertThrows(OPomboException.class, () -> {
            denunciaService.denunciar(denuncia);
        });

        assertEquals("Você só pode denunciar a mensagem uma vez.", exception.getMessage());
        verify(denunciaRepository, times(0)).save(denuncia);
    }

    @Test
    public void testAlterarDenuncia_Sucesso() throws OPomboException {
        Denuncia denunciaAlterada = new Denuncia();
        denunciaAlterada.setId(1);
        denunciaAlterada.setSituacao(SituacaoDenuncia.BLOQUEADA);
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denunciaAlterada);

        Denuncia resultado = denunciaService.alterar(denunciaAlterada);

        assertNotNull(resultado);
        assertEquals(SituacaoDenuncia.BLOQUEADA, resultado.getSituacao());
        verify(denunciaRepository, times(1)).save(denunciaAlterada);
    }

    @Test
    public void testExcluirDenuncia_Sucesso() throws OPomboException {
        when(denunciaRepository.findById(anyInt())).thenReturn(Optional.of(denuncia));
        doNothing().when(denunciaRepository).deleteById(anyInt());

        denunciaService.excluir(1);

        verify(denunciaRepository, times(1)).deleteById(1);
    }

    @Test
    public void testExcluirDenuncia_Falha_DenunciaNaoEncontrada() throws OPomboException {
        when(denunciaRepository.findById(anyInt())).thenReturn(Optional.empty());

        OPomboException exception = assertThrows(OPomboException.class, () -> {
            denunciaService.excluir(1);
        });

        assertEquals("Denúncia não encontrada!", exception.getMessage());
        verify(denunciaRepository, times(0)).deleteById(1);
    }


    @Test
    public void testAceitarDenuncia_Sucesso() throws OPomboException {
        when(denunciaRepository.findById(anyInt())).thenReturn(Optional.of(denuncia));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        boolean resultado = denunciaService.aceitarDenuncia(1);

        assertTrue(resultado);
        assertEquals(SituacaoDenuncia.BLOQUEADA, denuncia.getSituacao());
        verify(denunciaRepository, times(1)).save(denuncia);
    }

    @Test
    public void testRejeitarDenuncia_Sucesso() throws OPomboException {
        when(denunciaRepository.findById(anyInt())).thenReturn(Optional.of(denuncia));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        boolean resultado = denunciaService.rejeitarDenuncia(1);

        assertTrue(resultado);
        assertEquals(SituacaoDenuncia.REJEITADA, denuncia.getSituacao());
        verify(denunciaRepository, times(1)).save(denuncia);
    }
}
