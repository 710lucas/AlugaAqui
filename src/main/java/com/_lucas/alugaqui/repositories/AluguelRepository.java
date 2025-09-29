package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {

    @Query("SELECT a FROM Aluguel a WHERE (a.locador = :locador OR a.locatario = :locatario) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Aluguel> findAllByLocadorOrLocatarioAndOptionalFilters(Usuario locador, Usuario locatario, StatusAluguel status, Pageable pageable);

    Collection<Aluguel> findAllByLocadorOrLocatario(Usuario locador, Usuario locatario);
}