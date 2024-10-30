package com.tarefa.opombo.service;

import com.tarefa.opombo.factories.UsuarioFactory;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class UsuarioServiceTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = usuarioRepository.save(UsuarioFactory.criarUsuario());
    }

    @AfterEach
    public void tearDown() {
        usuarioRepository.deleteAll();
    }

    @Test
    public void testSalvarUsuarioComCpfJaUtilizado() {

    }

    @Test
    void buscarComSeletor() {
    }

    @Test
    void contarPaginas() {
    }

    @Test
    void testSalvarCpfJaUtilizado() {

    }
}