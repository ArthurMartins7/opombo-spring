package com.tarefa.opombo.model.repository;

import com.tarefa.opombo.factories.UsuarioFactory;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import jakarta.validation.ConstraintViolationException;
import org.h2.constraint.Constraint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    Usuario usuario;

    @Test
    void test() {}

    @BeforeEach
    public void setUp() {
        usuario = usuarioRepository.save(UsuarioFactory.criarUsuario());
    }

    @AfterEach
    public void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar um novo usuário com sucesso")
    public void testInserirTodosCamposPreenchidos() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste");
        usuario.setCpf("25310875085");
        usuario.setEmail("usuario@email.com");
        usuario.setSenha("senha123");
        usuario.setPerfilAcesso(PerfilAcesso.GERAL);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        assertThat(usuarioSalvo.getId()).isNotNull();
        assertThat(usuarioSalvo.getNome()).isEqualTo("Usuario Teste");
        assertThat(usuarioSalvo.getCpf()).isEqualTo("25310875085");
        assertThat(usuarioSalvo.getEmail()).isEqualTo("usuario@email.com");
        assertThat(usuarioSalvo.getSenha()).isEqualTo("senha123");
        assertThat(usuarioSalvo.getPerfilAcesso()).isEqualTo(PerfilAcesso.GERAL);
    }

    @Test
    @DisplayName("Não pode salvar um usuario com nome nulo")
    public void testSalvarNomeInvalidoNulo() {
        Usuario u = new Usuario();
        u.setPerfilAcesso(PerfilAcesso.ADMIN);
        u.setNome(null);
        u.setEmail("usuario2@example.com");
        u.setSenha("usuario123");
        u.setCpf("45541930987");

        assertThatThrownBy(() -> usuarioRepository.save(u)).isInstanceOf(ConstraintViolationException.class);

    }

    @Test
    @DisplayName("Não pode salvar um usuario com email invalido")
    public void testSalvarEmailInvalidoNulo() {
        Usuario u = new Usuario();
        u.setPerfilAcesso(PerfilAcesso.GERAL);
        u.setNome("Usuario teste email invalido");
        u.setEmail("usuarioexample.com");
        u.setSenha("usuario123");
        u.setCpf("27370724093");

        assertThatThrownBy(() -> usuarioRepository.save(u))
                .isInstanceOf(ConstraintViolationException.class);

    }

    @Test
    @DisplayName("Não pode salvar um usuario com cpf invalido")
    public void testSalvarCpfInvalido() {
        Usuario u = new Usuario();
        u.setPerfilAcesso(PerfilAcesso.GERAL);
        u.setNome("Usuario teste cpf invalido");
        u.setEmail("usuario@example.com");
        u.setSenha("usuario123");
        u.setCpf("1482696592");

        assertThatThrownBy(() -> usuarioRepository.save(u))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Deve lançar uma excessão pois o nome tem menos que 3 caracteres")
    void deveFalharAoSalvarUsuarioComNomeMenorQue3Caracteres() {
        Usuario usuario = new Usuario();
        usuario.setCpf("26253943669");
        usuario.setEmail("teste3caracteres@teste.com");
        usuario.setSenha("senha123");
        usuario.setNome("Jo");
        usuario.setPerfilAcesso(PerfilAcesso.GERAL);

        assertThrows(ConstraintViolationException.class, () -> {
            usuarioRepository.saveAndFlush(usuario);
        });
    }

    @Test
    @DisplayName("Deve lançar uma excessão pois o nome tem mais que 100 caracteres")
    void deveFalharAoSalvarUsuarioComNomeMaiorQue100Caracteres() {
        Usuario usuario = new Usuario();
        usuario.setCpf("30218552610");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("senha123");
        usuario.setNome("Esse nome tem que ter mais de cem caracteres para não passar no teste de validação do nome portanto vou ficar enrolando aqui.");
        usuario.setPerfilAcesso(PerfilAcesso.GERAL);

        assertThrows(ConstraintViolationException.class, () -> {
            usuarioRepository.saveAndFlush(usuario);
        });
    }

    @Test
    @DisplayName("Deve lançar uma excessão pois a senha está em branco")
    void deveFalharAoSalvarUsuarioComSenhaEmBranco() {
        // Dado
        Usuario usuario = new Usuario();
        usuario.setCpf("28201431057");
        usuario.setEmail("testesenha@teste.com");
        usuario.setSenha("");
        usuario.setNome("Senha em branco");
        usuario.setPerfilAcesso(PerfilAcesso.GERAL);


        assertThrows(ConstraintViolationException.class, () -> {
            usuarioRepository.saveAndFlush(usuario);
        });
    }
}