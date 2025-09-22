package com._lucas.alugaqui.security;

import com._lucas.alugaqui.exceptions.EmailNotFoundException;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UserDetails loadByEmail(String email) throws EmailNotFoundException {

        Usuario usuario = usuarioRepository.findUsuarioByEmail(email);

        if (usuario == null) {
            throw new EmailNotFoundException(email);
        }

        return new User(
                usuario.getEmail(), usuario.getSenha(), new ArrayList<>()
        );

    }

}
