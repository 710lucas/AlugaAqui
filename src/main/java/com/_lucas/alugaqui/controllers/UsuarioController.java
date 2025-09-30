package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.UsuarioCreateDTO;
import com._lucas.alugaqui.DTOs.UsuarioResponseDTO;
import com._lucas.alugaqui.DTOs.UsuarioUpdateDTO;
import com._lucas.alugaqui.models.Usuario.Usuario;
import com._lucas.alugaqui.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController (UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/")
    public UsuarioResponseDTO create (@Valid @RequestBody UsuarioCreateDTO createDTO) {
        return this.usuarioService.create(createDTO);
    }

    @GetMapping("/{id}")
    public UsuarioResponseDTO get (@PathVariable Long id) {
        return this.usuarioService.get(id);
    }

    @GetMapping()
    public Collection<UsuarioResponseDTO> getAll (){
        return this.usuarioService.getAll();
    }

    @PatchMapping("/{id}")
    public UsuarioResponseDTO update (@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO updateDTO) {
        return this.usuarioService.update(id, updateDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id) {
        this.usuarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
