package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaResponseDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.services.CasaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication; // Adicionado import
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/casas")
public class CasaController {

    private final CasaService casaService;

    public CasaController (CasaService casaService){
        this.casaService = casaService;
    }

    @PostMapping()
    public CasaResponseDTO create (@RequestBody @Valid CasaCreateDTO createDTO, Authentication authentication){
        String locadorEmail = authentication.getName();
        return this.casaService.create(createDTO, locadorEmail);
    }

    @GetMapping("/{id}")
    public CasaResponseDTO get (@PathVariable Long id) {
        return this.casaService.get(id);
    }

    @GetMapping("/")
    public Collection<CasaResponseDTO> getAll (Authentication authentication) { // Modificado
        String userEmail = authentication.getName();
        return this.casaService.getAll(userEmail);
    }

    @PatchMapping("/{id}")
    public CasaResponseDTO update (@PathVariable Long id, @Valid @RequestBody CasaUpdateDTO updateDTO, Authentication authentication) {
        String locadorEmail = authentication.getName();
        return this.casaService.update(id, updateDTO, locadorEmail);
    }

    @DeleteMapping("/{id}")
    public void delete (@PathVariable Long id, Authentication authentication) {
        String locadorEmail = authentication.getName();
        this.casaService.delete(id, locadorEmail);
    }
}