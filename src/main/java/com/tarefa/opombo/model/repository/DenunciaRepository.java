package com.tarefa.opombo.model.repository;

import com.tarefa.opombo.model.entity.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Integer>, JpaSpecificationExecutor<Denuncia> {

    boolean existsByMensagemIdAndDenuncianteId(String mensagemId, Integer denuncianteId);

}
