package com.tarefa.opombo.model.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
/* O @DataJpaTest É PRA USAR O h2*/
//@DataJpaTest

//Anotação usada para subir uma base local (ex.: MySQL)
@SpringBootTest

@ActiveProfiles("test")
class MensagemRepositoryTest {

    @Test
    void findByUsuario() {
    }
}