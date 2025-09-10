package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.models.Casa.Casa;
import com._lucas.alugaqui.services.CasaService;
import jakarta.validation.Valid;
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
    public Casa create (@RequestBody @Valid CasaCreateDTO createDTO){
        return this.casaService.create(createDTO);
    }

    @GetMapping("/{id}")
    public Casa get (@PathVariable Long id) {
        return this.casaService.get(id);
    }

    @GetMapping("/")
    public Collection<Casa> getAll () {
        return this.casaService.getAll();
    }

    @PatchMapping("/{id}")
    public Casa update (@PathVariable Long id, @Valid @RequestBody CasaUpdateDTO updateDTO) {
        return this.casaService.update(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    public void delete (@PathVariable Long id) {
        this.casaService.delete(id);
    }



}
