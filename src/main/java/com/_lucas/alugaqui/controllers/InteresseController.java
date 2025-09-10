package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
import com._lucas.alugaqui.models.Interesse.Interesse;
import com._lucas.alugaqui.services.InteresseService;
import jakarta.validation.Valid;
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
    public Interesse create (@Valid @RequestBody InteresseCreateDTO createDTO){
        return this.interesseService.create(createDTO);
    }

    @GetMapping("/{id}")
    public Interesse get (@PathVariable Long id) {
        return this.interesseService.get(id);
    }

    @GetMapping("/")
    public Collection<Interesse> getAll () {
        return this.interesseService.getAll();
    }

    @PatchMapping("/{id}")
    public Interesse update (@PathVariable Long id, @Valid @RequestBody InteresseUpdateDTO updateDTO) {
        return this.interesseService.update(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    public void delete (@PathVariable Long id) {
        this.interesseService.delete(id);
    }


}
