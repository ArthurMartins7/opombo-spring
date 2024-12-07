package com.tarefa.opombo.model.repository;


import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.enums.PerfilAcesso;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;


import com.tarefa.opombo.model.entity.Mensagem;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class MensagemRepositoryTest {


    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    Usuario usuario;
    Mensagem mensagem;


    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setNome("Arthur Bowens");
        usuario.setEmail("arthur@gmail.com");
        usuario.setCpf("61461611016");
        usuario.setSenha("arthur123");
        usuario.setPerfilAcesso(PerfilAcesso.GERAL);
        usuarioRepository.save(usuario);

        mensagem = new Mensagem();
        mensagem.setTexto("Mensagem original");
        mensagem.setUsuario(usuario);
        mensagemRepository.save(mensagem);
    }

    @Test
    @DisplayName("Deve salvar uma nova mensagem com sucesso")
    public void deveSalvarMensagemComSucesso() {
        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setTexto("Esta é uma nova mensagem de teste.");
        novaMensagem.setUsuario(usuario);

        Mensagem mensagemSalva = mensagemRepository.save(novaMensagem);

        Optional<Mensagem> mensagemBuscada = mensagemRepository.findById(mensagemSalva.getId());
        assertThat(mensagemBuscada).isPresent();
        assertThat(mensagemSalva.getId()).isNotNull();
        assertThat(mensagemSalva.getDataHoraCriacao()).isNotNull();
        assertThat(mensagemSalva.getTexto()).isEqualTo("Esta é uma nova mensagem de teste.");
        assertThat(mensagemSalva.getUsuario().getNome()).isEqualTo("Arthur Bowens");
        assertThat(mensagemSalva.getQuantidadeLikes()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deve lançar um excessão pois o texto está em branco")
    void deveFalharAoSalvarMensagemComTextoEmBranco() {
        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setTexto("");
        novaMensagem.setUsuario(usuario);

        assertThatThrownBy(() -> mensagemRepository.saveAndFlush(novaMensagem))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Mensagem é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar um excessão pois o texto é nulo")
    public void deveFalharQuandoTextoForNulo() {
        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setTexto(null);
        novaMensagem.setUsuario(usuario);

        assertThatThrownBy(() -> mensagemRepository.saveAndFlush(novaMensagem))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Mensagem é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar um excessão pois o texto contém mais de 300 caracteres")
    public void deveFalharQuandoTextoForMaiorQue300Caracteres() {
        String texto = "a".repeat(301);

        Mensagem novaMensagem = new Mensagem();
        novaMensagem.setTexto(texto);
        novaMensagem.setUsuario(usuario);

        assertThrows(ConstraintViolationException.class, () -> {
            mensagemRepository.saveAndFlush(novaMensagem);
        });
    }

    @Test
    @DisplayName("Deve atualizar uma nova mensagem com sucesso")
    public void deveAtualizarMensagemComSucesso() {
        mensagem.setTexto("Mensagem atualizada com sucesso");
        Mensagem mensagemAtualizada = mensagemRepository.saveAndFlush(mensagem);

        Optional<Mensagem> mensagemBuscada = mensagemRepository.findById(mensagemAtualizada.getId());
        assertThat(mensagemBuscada).isPresent();
        assertThat(mensagemBuscada.get().getTexto()).isEqualTo("Mensagem atualizada com sucesso");
    }

    @Test
    @DisplayName("Deve lançar um excessão pois o texto é nulo")
    public void deveFalharQuandoAtualizarComTextoNulo() {
        mensagem.setTexto(null);

        assertThatThrownBy(() -> mensagemRepository.saveAndFlush(mensagem))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Mensagem é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar um excessão pois o texto está em branco")
    public void deveFalharQuandoAtualizarComTextoVazio() {
        mensagem.setTexto("");

        assertThatThrownBy(() -> mensagemRepository.saveAndFlush(mensagem))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Mensagem é obrigatória");
    }

    @Test
    @DisplayName("Deve lançar um excessão pois o texto contém mais de 300 caracteres")
    public void deveFalharQuandoAtualizarComTextoMaiorQue300Caracteres() {
        String texto = "a".repeat(301);
        mensagem.setTexto(texto);

        assertThrows(ConstraintViolationException.class, () -> {
            mensagemRepository.saveAndFlush(mensagem);
        });
    }

    @Test
    @DisplayName("Deve excluir mensagem com sucesso")
    public void deveExcluirMensagemComSucesso() {
        String mensagemId = mensagem.getId();
        mensagemRepository.deleteById(mensagemId);

        Optional<Mensagem> mensagemBuscada = mensagemRepository.findById(mensagemId);
        assertThat(mensagemBuscada).isNotPresent();
    }

    @Test
    @DisplayName("Deve falhar pois id não existe")
    public void deveFalharAoTentarExcluirMensagemComIdInexistente() {
        String idInexistente = "atrtrtt4343";

        assertThat(mensagemRepository.findById(idInexistente)).isNotPresent();
    }

    @Test
    @DisplayName("Deve falhar pois id está nulo")
    public void deveFalharAoTentarExcluirMensagemComIdNulo() {
        String idNulo = null;

        assertThrows(InvalidDataAccessApiUsageException.class, () -> mensagemRepository.deleteById(idNulo));
    }

}