package com.tarefa.opombo.service;

import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Base64;

@Service
public class ImagemService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    public String processarImagem(MultipartFile file) throws OPomboException {
        // Converte MultipartFile em byte[]
        byte[] imagemBytes;
        try {
            imagemBytes = file.getBytes();
        } catch (IOException e) {
            throw new OPomboException("Erro ao processar arquivo");
        }

        // Converte byte[] para Base64
        String base64Imagem = Base64.getEncoder().encodeToString(imagemBytes);

        return base64Imagem;
    }

    public <T> void salvarImagem(T usuarioOuMensagem, MultipartFile imagem, T id) throws OPomboException {

        if (usuarioOuMensagem instanceof Usuario) {
            Usuario usuarioComNovaImagem = (Usuario) usuarioOuMensagem;

            usuarioComNovaImagem = usuarioRepository
                    .findById((Integer) id)
                    .orElseThrow(() -> new OPomboException("Usuario não encontrada"));

            //Converter a imagem para base64
            String imagemBase64 = this.processarImagem(imagem);

            //Inserir a imagem na coluna imagemEmBase64 do usuario

            //TODO ajustar para fazer o upload
            usuarioComNovaImagem.setImagemEmBase64(imagemBase64);

            //Chamar cartaRepository para persistir a imagem no usuario
            usuarioRepository.save(usuarioComNovaImagem);

        } else if (usuarioOuMensagem instanceof Mensagem) {
            Mensagem mensagemComNovaImagem = (Mensagem) usuarioOuMensagem;

            mensagemComNovaImagem = mensagemRepository
                    .findById((String) id)
                    .orElseThrow(() -> new OPomboException("Mensagem não encontrada"));

            String imagemBase64 = this.processarImagem(imagem);

            mensagemComNovaImagem.setImagemEmBase64(imagemBase64);

            mensagemRepository.save(mensagemComNovaImagem);
        }

    }
}
