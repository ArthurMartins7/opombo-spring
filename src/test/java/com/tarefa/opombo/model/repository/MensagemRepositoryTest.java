package com.tarefa.opombo.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tarefa.opombo.factories.UsuarioFactory;
import com.tarefa.opombo.model.entity.Usuario;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.tarefa.opombo.model.entity.Mensagem;


@SpringBootTest
@ActiveProfiles("test")
class MensagemRepositoryTest {


    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    Usuario usuario;


    @BeforeEach
    public void setUp() {
        usuario = usuarioRepository.save((UsuarioFactory.criarUsuario()));
    }

    @AfterEach
    public void teardown() {
        mensagemRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve ser capaz de inserir uma nova mensagem")
    public void testInserirComSucesso() {
        Mensagem mensagem = new Mensagem();
        mensagem.setUsuario(usuario);
        mensagem.setTexto("Novo texto");

        Mensagem mensagemSalva = mensagemRepository.save(mensagem);

        assertThat(mensagemSalva.getId()).isNotNull();
        assertThat(mensagemSalva.getUsuario().getId()).isEqualTo(usuario.getId());
        assertThat(mensagemSalva.getTexto()).isEqualTo("Novo texto");
        }

        @Test
        @DisplayName("Não é capaz de inserir mensagem com conteudo maior que 300 caracteres")
        public void inserirComMais300() {

        Mensagem mensagem = new Mensagem();
        String texto = "A tecnologia tem revolucionado o modo como interagimos com o mundo ao nosso redor. Desde a automação de tarefas simples até o desenvolvimento de inteligência artificial avançada, o impacto é evidente em diversos setores, como educação, saúde e indústria. A conectividade global facilitou a troca de informações, criando oportunidades inéditas para inovação e colaboração.";
        mensagem.setUsuario(usuario);
        mensagem.setTexto(texto.repeat(301));

            assertThatThrownBy(() -> mensagemRepository.save(mensagem)).isInstanceOf(Exception.class);
        }
}