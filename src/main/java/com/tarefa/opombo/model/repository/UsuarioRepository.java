package com.tarefa.opombo.model.repository;

import com.tarefa.opombo.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByCpfAndIdNot(String cpf, Integer id);
    boolean existsByEmailAndIdNot(String email, Integer id);
}
