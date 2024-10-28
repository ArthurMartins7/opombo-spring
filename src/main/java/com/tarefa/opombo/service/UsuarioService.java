package com.tarefa.opombo.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.dto.UsuarioEditadoDTO;
import com.tarefa.opombo.model.entity.Mensagem;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.repository.MensagemRepository;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.model.seletor.UsuarioSeletor;
import com.tarefa.opombo.security.AuthorizationService;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> buscarComSeletor(UsuarioSeletor usuarioSeletor) {
        if (usuarioSeletor.temPaginacao()) {
            int numeroPagina = usuarioSeletor.getPagina();
            int tamanhoPagina = usuarioSeletor.getLimite();

            PageRequest pagina = PageRequest.of(numeroPagina - 1, tamanhoPagina, Sort.by(Sort.Direction.DESC, "criadoEm"));
            return usuarioRepository.findAll(usuarioSeletor, pagina).toList();
        }

        return usuarioRepository.findAll(usuarioSeletor, Sort.by(Sort.Direction.DESC, "criadoEm"));
    }

    public int contarPaginas(UsuarioSeletor usuarioSeletor) {
        return (int) usuarioRepository.count(usuarioSeletor);
    }

    public List<Usuario> buscarTodos() throws OPomboException {

        return usuarioRepository.findAll(Sort.by(Sort.Direction.DESC, "criadoEm"));
    }

    public Usuario buscarPorId(int id) throws OPomboException {
        authorizationService.verificarPerfilAcesso();

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new OPomboException("Usuário não encontrado."));
    }

    public Usuario salvar(Usuario usuario) throws OPomboException {

        List<Usuario> usuarios = usuarioRepository.findAll();

        for (Usuario u : usuarios) {
            if (u.getCpf().equals(usuario.getCpf())) {
                throw new OPomboException("Não pode utilizar um CPF já cadastrado!");
            } else if (u.getEmail().equals(usuario.getEmail())) {
                throw new OPomboException("Não pode utilizar um e-mail já cadastrado!");
            }
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario alterar(int idUsuario, UsuarioEditadoDTO usuarioEditadoDTO) throws OPomboException {
        authorizationService.verifiarCredenciaisUsuario(idUsuario);

        verificarCpfEmailJaUtilizados(usuarioEditadoDTO.getCpf(), usuarioEditadoDTO.getEmail(), idUsuario);

        Usuario usuarioEditado = usuarioRepository.findById(idUsuario).get();

        String senhaCifrada = passwordEncoder.encode(usuarioEditadoDTO.getSenha());

        usuarioEditado.setPerfilAcesso(usuarioEditadoDTO.getPerfilAcesso());
        usuarioEditado.setNome(usuarioEditadoDTO.getNome());
        usuarioEditado.setEmail(usuarioEditadoDTO.getEmail());
        usuarioEditado.setCpf(usuarioEditadoDTO.getCpf());
        usuarioEditado.setSenha(senhaCifrada);

        LocalDateTime dateTime = LocalDateTime.now();
        usuarioEditado.setDataUltimaModificacao(dateTime);


        return usuarioRepository.save(usuarioEditado);
    }


    public void excluir(int id) throws OPomboException {
        authorizationService.verifiarCredenciaisUsuario(id);

        List<Mensagem> mensagens = mensagemRepository.findAll();

        for (Mensagem mensagem : mensagens) {
            if (mensagem.getUsuario().getId() == id) {
                throw new OPomboException("Não pode excluir um usuário que já criou uma mensagem!");
            }
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Usuário não encontrado" + username)
                );
    }

    public void verificarCpfEmailJaUtilizados(String cpf, String email, Integer idAtual) throws OPomboException {
        if (usuarioRepository.existsByCpfAndIdNot(cpf, idAtual)) {
            throw new OPomboException("Não pode utilizar um CPF já cadastrado!");
        }
        if (usuarioRepository.existsByEmailAndIdNot(email, idAtual)) {
            throw new OPomboException("Não pode utilizar um e-mail já cadastrado!");
        }
    }
}


