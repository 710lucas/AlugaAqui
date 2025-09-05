package com._lucas.alugaqui.repositories;

import com._lucas.alugaqui.models.Aluguel.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {
}
