package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {
    Collection<Interesse> findAllByLocatario(Usuario locatario);

    Collection<Interesse> findAllByCasa_Locador(Usuario locador);
}