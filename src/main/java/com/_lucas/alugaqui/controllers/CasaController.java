package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaResponseDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.services.CasaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping()
    public Page<CasaResponseDTO> getAll (
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Integer minQuartos,
            @RequestParam(required = false) Status status,
            @PageableDefault(sort = "nome", size = 10, page = 1) Pageable pageable,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return this.casaService.getAll(userEmail, tipo, minQuartos, status, pageable);
    }

    @PatchMapping("/{id}")
    public CasaResponseDTO update (@PathVariable Long id, @Valid @RequestBody CasaUpdateDTO updateDTO, Authentication authentication) {
        String locadorEmail = authentication.getName();
        return this.casaService.update(id, updateDTO, locadorEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id, Authentication authentication) {
        String locadorEmail = authentication.getName();
        this.casaService.delete(id, locadorEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}