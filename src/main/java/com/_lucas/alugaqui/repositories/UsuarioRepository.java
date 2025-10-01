package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findUsuarioByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE " +
            "(:nome IS NULL OR u.nome LIKE %:nome%) AND " +
            "(:email IS NULL OR u.email LIKE %:email%) AND " +
            "(:role IS NULL OR u.role = :role)")
    Page<Usuario> findAllByOptionalFilters(String nome, String email, Role role, Pageable pageable);
}