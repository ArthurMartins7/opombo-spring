package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    public List<Mensagem> buscarTodos() {
        return mensagemRepository.findAll();
    }

    public Mensagem buscarPorId(String id) throws OPomboException {
        return mensagemRepository.findById(id).orElseThrow(() -> new OPomboException("Mensagem não encontrada."));
    }

    public Mensagem salvar(Mensagem mensagem) throws OPomboException {
        return mensagemRepository.save(mensagem);
    }

    public Mensagem alterar(Mensagem mensagemAlterada) throws OPomboException {
        return mensagemRepository.save(mensagemAlterada);
    }

    public void excluir(String id) {
        mensagemRepository.deleteById(id);
    }
}
