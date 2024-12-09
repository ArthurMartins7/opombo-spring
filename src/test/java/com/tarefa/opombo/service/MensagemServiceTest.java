package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.security.AuthorizationService;
import com.tarefa.opombo.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MensagemServiceTest {

    @Mock
    private MensagemRepository mensagemRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    public AuthenticationService authenticationService;



    @InjectMocks
    private MensagemService mensagemService;

    private Mensagem mensagem;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNome("Test User");

        mensagem = new Mensagem();
        mensagem.setId("123");
        mensagem.setUsuario(usuario);
        mensagem.setCurtidas(new ArrayList<>());
    }

    @Test
    void testDarLikeMensagem_Successo() throws OPomboException {
        // Setup do mock
        when(authenticationService.getUsuarioAutenticado()).thenReturn(usuario);
        when(mensagemRepository.findById("123")).thenReturn(java.util.Optional.of(mensagem));

        boolean curtiu = mensagemService.darLike("123");

        assertTrue(curtiu);
        assertEquals(1, mensagem.getQuantidadeLikes());
        verify(mensagemRepository, times(1)).save(mensagem);
    }

    @Test
    void testDarLikeMensagem_Falha() throws OPomboException {
        when(authenticationService.getUsuarioAutenticado()).thenReturn(usuario);
        when(mensagemRepository.findById("123")).thenReturn(java.util.Optional.of(mensagem));

        mensagem.getCurtidas().add(usuario);
        mensagem.setQuantidadeLikes(1);

        boolean curtiu = mensagemService.darLike("123");

        assertFalse(curtiu);
        assertEquals(0, mensagem.getQuantidadeLikes());
        verify(mensagemRepository, times(1)).save(mensagem);
    }
}
