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

}