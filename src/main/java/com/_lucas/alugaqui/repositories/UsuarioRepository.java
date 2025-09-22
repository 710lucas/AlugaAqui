package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findUsuarioByEmail(String email);
}
