package com.tarefa.opombo.model.seletor;

import com.tarefa.opombo.model.entity.Denuncia;
import com.tarefa.opombo.model.enums.MotivoDenuncia;
import com.tarefa.opombo.model.enums.SituacaoDenuncia;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DenunciaSeletor extends BaseSeletor implements Specification<Denuncia> {

    private String texto;
    private MotivoDenuncia motivo;
    private SituacaoDenuncia situacao;
    private LocalDate dataInicialCriacao;
    private LocalDate dataFinalCriacao;

    @Override
    public Predicate toPredicate(Root<Denuncia> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if(this.getTexto() != null && this.getTexto().trim().length() > 0) {
            predicates.add(cb.like(root.join("mensagem").get("texto"), "%" + this.getTexto() + "%"));
        }


        if(this.getMotivo() != null){
            predicates.add(cb.equal(root.get("motivo"), this.getMotivo()));
        }

        if(this.getSituacao() != null){
            predicates.add(cb.equal(root.get("situacao"), this.getSituacao()));
        }

        aplicarFiltroPeriodo(root, cb, predicates, this.getDataInicialCriacao(), this.getDataFinalCriacao(), "dataHoraCriacao");

        return cb.and(predicates.toArray(new Predicate[0]));
    }

}
