package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {

    @Query("SELECT i FROM Interesse i WHERE i.locatario = :locatario AND " +
            "(:status IS NULL OR i.status = :status)")
    Page<Interesse> findAllByLocatarioAndOptionalFilters(Usuario locatario, StatusInteresse status, Pageable pageable);

    @Query("SELECT i FROM Interesse i WHERE i.casa.locador = :locador AND " +
            "(:status IS NULL OR i.status = :status)")
    Page<Interesse> findAllByCasaLocadorAndOptionalFilters(Usuario locador, StatusInteresse status, Pageable pageable);

    Collection<Interesse> findAllByLocatario(Usuario locatario);
    Collection<Interesse> findAllByCasa_Locador(Usuario locador);
}