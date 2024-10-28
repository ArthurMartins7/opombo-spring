package com.tarefa.opombo.auth;


import com.tarefa.opombo.exception.OPomboException;
import com.tarefa.opombo.model.entity.Usuario;
import com.tarefa.opombo.model.repository.UsuarioRepository;
import com.tarefa.opombo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthenticationService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final JwtService jwtService;

    public AuthenticationService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String authenticate(Authentication authentication) {
        return jwtService.getGenerateToken(authentication);
    }

    public Usuario getUsuarioAutenticado() throws OPomboException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAutenticado = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof Jwt) {
                String email = ((Jwt) principal).getSubject();
                System.out.println("email value:" + email);
                Optional<Usuario> usuario = Optional.ofNullable(usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não econtrado!")));

                usuarioAutenticado = usuario.get();

            }
        }

        return usuarioAutenticado;
    }
}
