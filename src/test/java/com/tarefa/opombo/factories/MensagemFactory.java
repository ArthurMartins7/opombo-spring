package com.tarefa.opombo.factories;

import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;



    public class MensagemFactory {
        public static Mensagem criarMensagem(Usuario usuario) {
            Mensagem m = new Mensagem();
            m.setUsuario(usuario);
            m.setTexto("Novo texto");

            return m;
        }
    }
