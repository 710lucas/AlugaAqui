package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Casa.Casa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CasaRepository extends JpaRepository<Casa, Long> {
}
