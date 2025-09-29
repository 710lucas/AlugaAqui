package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.models.Usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface CasaRepository extends JpaRepository<Casa, Long> {

    Collection<Casa> findAllByLocador(Usuario locador);

    @Query("SELECT c FROM Casa c WHERE " +
            "(:tipo IS NULL OR c.tipo = :tipo) AND " +
            "(:minQuartos IS NULL OR c.quartos >= :minQuartos) AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Casa> findAllByOptionalFilters(String tipo, Integer minQuartos, Status status, Pageable pageable);

    @Query("SELECT c FROM Casa c WHERE c.locador = :locador AND " +
            "(:tipo IS NULL OR c.tipo = :tipo) AND " +
            "(:minQuartos IS NULL OR c.quartos >= :minQuartos) AND " +
            "(:status IS NULL OR c.status = :status)")
    Page<Casa> findAllByLocadorAndOptionalFilters(Usuario locador, String tipo, Integer minQuartos, Status status, Pageable pageable);
}