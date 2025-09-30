package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelResponseDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import com._lucas.alugaqui.services.AluguelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController (AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    @PostMapping
    public AluguelResponseDTO create (@Valid @RequestBody AluguelCreateDTO createDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        return this.aluguelService.create(createDTO, userEmail);
    }

    @GetMapping("/{id}")
    public AluguelResponseDTO get (@PathVariable Long id) {
        return this.aluguelService.get(id);
    }

    @GetMapping
    public Page<AluguelResponseDTO> getAll (
            @RequestParam(required = false) StatusAluguel status,
            @PageableDefault(sort = "dataInicio", size = 10) Pageable pageable,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return this.aluguelService.getAll(userEmail, status, pageable);
    }

    @PatchMapping("/{id}")
    public AluguelResponseDTO update (@PathVariable Long id, @Valid @RequestBody AluguelUpdateDTO updateDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        return this.aluguelService.update(id, updateDTO, userEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        this.aluguelService.delete(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}