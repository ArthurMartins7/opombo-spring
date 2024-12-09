package com.tarefa.opombo.model.repository;

import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, String>, JpaSpecificationExecutor<Mensagem> {

    List<Mensagem> findByUsuario(Usuario usuario);

    @Query("SELECT m FROM Mensagem m WHERE m.bloqueado = false AND m.isDeleted = false")
    List<Mensagem> findAllNotBlockedAndNotDeleted();

}
