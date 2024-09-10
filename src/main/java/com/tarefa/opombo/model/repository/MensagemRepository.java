package com.tarefa.opombo.model.repository;

import com.tarefa.opombo.model.entity.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, String>, JpaSpecificationExecutor<Mensagem> {

}
