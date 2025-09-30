package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.UsuarioCreateDTO;
import com._lucas.alugaqui.DTOs.UsuarioResponseDTO;
import com._lucas.alugaqui.DTOs.UsuarioUpdateDTO;
import com._lucas.alugaqui.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de Usuários (Locadores e Locatários)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController (UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cria um novo usuário",
            description = "Cria um novo usuário (Locador ou Locatário). Requer autenticação, mas a criação inicial é permitida a todos para auto-cadastro.")
    @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @PostMapping("/")
    public UsuarioResponseDTO create (@Valid @RequestBody UsuarioCreateDTO createDTO) {
        return this.usuarioService.create(createDTO);
    }

    @Operation(summary = "Busca um usuário por ID",
            description = "Retorna os detalhes de um usuário específico.")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @GetMapping("/{id}")
    public UsuarioResponseDTO get (@PathVariable Long id) {
        return this.usuarioService.get(id);
    }

    @Operation(summary = "Lista todos os usuários",
            description = "Retorna uma lista de todos os usuários cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    @GetMapping()
    public Collection<UsuarioResponseDTO> getAll (){
        return this.usuarioService.getAll();
    }

    @Operation(summary = "Atualiza um usuário por ID",
            description = "Atualiza parcialmente as informações de um usuário. Requer autenticação.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso Negado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PatchMapping("/{id}")
    public UsuarioResponseDTO update (@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO updateDTO) {
        return this.usuarioService.update(id, updateDTO);
    }

    @Operation(summary = "Deleta um usuário por ID",
            description = "Remove um usuário do sistema. Requer autenticação (geralmente ADMIN).")
    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso (sem conteúdo)")
    @ApiResponse(responseCode = "403", description = "Acesso Negado")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id) {
        this.usuarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}