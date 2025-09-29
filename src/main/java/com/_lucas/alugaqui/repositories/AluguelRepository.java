package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
    Collection<Aluguel> findAllByLocadorOrLocatario(Usuario locador, Usuario locatario);
}