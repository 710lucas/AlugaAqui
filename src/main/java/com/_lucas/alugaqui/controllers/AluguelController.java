package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.models.Aluguel.Aluguel;
import com._lucas.alugaqui.services.AluguelService;
import jakarta.validation.Valid;
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
    public Aluguel create (@Valid @RequestBody AluguelCreateDTO createDTO) {
        return this.aluguelService.create(createDTO);
    }

    @GetMapping("/{id}")
    public Aluguel get (@PathVariable Long id) {
        return this.aluguelService.get(id);
    }

    @GetMapping
    public Collection<Aluguel> getAll () {
        return this.aluguelService.getAll();
    }

    @PatchMapping("/{id}")
    public Aluguel update (@PathVariable Long id, @Valid @RequestBody AluguelUpdateDTO updateDTO) {
        return this.aluguelService.update(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    public void delete (@PathVariable Long id) {
        this.aluguelService.delete(id);
    }

}
