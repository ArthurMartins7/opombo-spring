package com.tarefa.opombo.model.repository;

import com.tarefa.opombo.factories.UsuarioFactory;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("Não pode salvar um usuario com nome nulo")
    public void testSalvarNomeInvalidoNulo() {
        Usuario u = new Usuario();
        u.setPerfilAcesso(PerfilAcesso.ADMIN);
        u.setNome(null);
        u.setEmail("usuario2@example.com");
        u.setSenha("usuario123");
        u.setCpf("45541930987");

        assertThatThrownBy(() -> usuarioRepository.save(u)).isInstanceOf(Exception.class);

    }



}