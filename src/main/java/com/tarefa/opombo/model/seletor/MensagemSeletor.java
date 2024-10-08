package com.tarefa.opombo.model.seletor;

import com.tarefa.opombo.model.entity.Mensagem;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MensagemSeletor extends BaseSeletor implements Specification<Mensagem> {

    private Boolean bloqueado;
    private String texto;
    private LocalDateTime dataInicialCriacao;
    private LocalDateTime dataFinalCriacao;

    @Override
    public Predicate toPredicate(Root<Mensagem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (this.getBloqueado() != null) {
            predicates.add(cb.like(root.get("bloqueado"), "%" + this.getBloqueado() + "%"));
        }

        if(this.getTexto() != null && !this.getTexto().trim().isEmpty()) {
            predicates.add(cb.like(root.get("texto"), "%" + this.getTexto() + "%"));
        }

        aplicarFiltroPeriodo(root, cb, predicates, this.getDataInicialCriacao(), this.getDataFinalCriacao(), "dataHoraCriacao");

        return cb.and(predicates.toArray(new Predicate[0]));
    }

}
