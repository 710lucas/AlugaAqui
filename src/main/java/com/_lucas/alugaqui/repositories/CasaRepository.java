package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CasaRepository extends JpaRepository<Casa, Long> {
    Collection<Casa> findAllByLocador(Usuario locador);
}