package com._lucas.alugaqui.util;

import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        return args -> {
            if(usuarioRepository.findUsuarioByEmail("admin@admin.com") == null) {
                Usuario usuario = new Usuario(
                        "admin",
                        "admin@admin.com",
                        "00912345678",
                        Role.LOCADOR,
                        bCryptPasswordEncoder.encode("admin")
                );
                usuarioRepository.save(usuario);
            }
        };
    }

}
