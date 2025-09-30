package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseResponseDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import com._lucas.alugaqui.services.InteresseService;
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
@RequestMapping("/interesses")
public class InteresseController {

    private final InteresseService interesseService;

    public InteresseController (InteresseService interesseService) {
        this.interesseService = interesseService;
    }

    @PostMapping("/")
    public InteresseResponseDTO create (@Valid @RequestBody InteresseCreateDTO createDTO, Authentication authentication){
        String locatarioEmail = authentication.getName();
        return this.interesseService.create(createDTO, locatarioEmail);
    }

    @GetMapping("/{id}")
    public InteresseResponseDTO get (@PathVariable Long id) {
        return this.interesseService.get(id);
    }

    @GetMapping("/")
    public Page<InteresseResponseDTO> getAll (
            @RequestParam(required = false) StatusInteresse status,
            @PageableDefault(sort = "createdAt", size = 10) Pageable pageable,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return this.interesseService.getAll(userEmail, status, pageable);
    }

    @PatchMapping("/{id}")
    public InteresseResponseDTO update (@PathVariable Long id, @Valid @RequestBody InteresseUpdateDTO updateDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        return this.interesseService.update(id, updateDTO, userEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        this.interesseService.delete(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}