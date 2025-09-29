package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelResponseDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.services.AluguelService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication; // Adicionado import
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
    public Collection<AluguelResponseDTO> getAll (Authentication authentication) { // Modificado
        String userEmail = authentication.getName();
        return this.aluguelService.getAll(userEmail);
    }

    @PatchMapping("/{id}")
    public AluguelResponseDTO update (@PathVariable Long id, @Valid @RequestBody AluguelUpdateDTO updateDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        return this.aluguelService.update(id, updateDTO, userEmail);
    }

    @DeleteMapping("/{id}")
    public void delete (@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        this.aluguelService.delete(id, userEmail);
    }

}