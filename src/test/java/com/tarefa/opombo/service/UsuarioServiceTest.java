package com.tarefa.opombo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.UsuarioEditadoDTO;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.model.seletor.UsuarioSeletor;
import com.tarefa.opombo.security.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MensagemRepository mensagemRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private List<Usuario> usuarios;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        usuarios = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Usuario usuario = new Usuario();
            usuario.setId(i);
            usuario.setNome("Usuario " + i);
            usuario.setCpf("13663280942" + i);
            usuario.setEmail("usuario" + i + "@email.com");
            usuario.setPerfilAcesso(PerfilAcesso.GERAL);

            usuarios.add(usuario);
        }
        when(usuarioRepository.findAll()).thenReturn(usuarios);
    }

    @Test
    @DisplayName("Deve retornar todos os usuários")
    void testBuscarTodos() throws OPomboException {
        List<Usuario> resultado = usuarioService.buscarTodos();
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve retornar um usuário por ID")
    void testBuscarPorId() throws OPomboException {
        Usuario usuario = usuarios.get(0);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.buscarPorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void testBuscarPorIdUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        OPomboException exception = assertThrows(OPomboException.class, () -> {
            usuarioService.buscarPorId(1);
        });

        assertThat(exception.getMessage()).isEqualTo("Usuário não encontrado.");
    }

    @Test
    @DisplayName("Deve salvar um novo usuário")
    void testSalvarUsuario() throws OPomboException {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Novo Usuário");
        novoUsuario.setCpf("12331658013");
        novoUsuario.setEmail("novo@gmail.com");
        novoUsuario.setPerfilAcesso(PerfilAcesso.GERAL);


        when(usuarioRepository.save(novoUsuario)).thenReturn(novoUsuario);

        Usuario resultado = usuarioService.salvar(novoUsuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Novo Usuário");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar usuário com CPF ou e-mail duplicados")
    void testSalvarUsuarioCpfOuEmailDuplicado() {
        Usuario usuarioDuplicado = usuarios.get(0);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        OPomboException exception = assertThrows(OPomboException.class, () -> {
            usuarioService.salvar(usuarioDuplicado);
        });

        assertThat(exception.getMessage()).contains("já cadastrado");
    }

    @Test
    @DisplayName("Deve excluir um usuário com sucesso")
    void testExcluirUsuario() throws OPomboException {
        doNothing().when(authorizationService).verifiarCredenciaisUsuario(1);
        when(mensagemRepository.findAll()).thenReturn(new ArrayList<>());

        usuarioService.excluir(1);

        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário com mensagens associadas")
    void testExcluirUsuarioComMensagens() {
        Usuario usuario = usuarios.get(0);
        Mensagem mensagem = new Mensagem();
        mensagem.setUsuario(usuario);

        when(mensagemRepository.findAll()).thenReturn(List.of(mensagem));

        OPomboException exception = assertThrows(OPomboException.class, () -> {
            usuarioService.excluir(1);
        });

        assertThat(exception.getMessage()).isEqualTo("Não pode excluir um usuário que já criou uma mensagem!");
    }
}
