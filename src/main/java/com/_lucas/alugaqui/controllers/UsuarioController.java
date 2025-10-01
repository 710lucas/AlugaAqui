package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.UsuarioCreateDTO;
import com._lucas.alugaqui.DTOs.UsuarioResponseDTO;
import com._lucas.alugaqui.DTOs.UsuarioUpdateDTO;
import com._lucas.alugaqui.models.Usuario.Role;
import com._lucas.alugaqui.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @Operation(summary = "Lista todos os usuários com filtros e paginação",
            description = "Retorna uma lista paginada de todos os usuários cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada")
    @GetMapping()
    public Page<UsuarioResponseDTO> getAll (
            @Parameter(description = "Filtro parcial por nome do usuário.")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Filtro parcial por email do usuário.")
            @RequestParam(required = false) String email,
            @Parameter(description = "Filtro por papel do usuário (LOCATARIO ou LOCADOR).")
            @RequestParam(required = false) Role role,
            @Parameter(description = "Número da página (inicia em 0). Default: 0.", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página. Default: 100.", example = "100")
            @RequestParam(defaultValue = "100") int size,
            @Parameter(description = "Propriedade para ordenação, seguido de ',' e direção (ASC ou DESC). Default: nome,ASC.", example = "nome,ASC")
            @RequestParam(defaultValue = "nome") String sort,
            @PageableDefault(sort = "nome", size = 100) @Parameter(hidden = true) Pageable pageable
    ){
        return this.usuarioService.getAll(nome, email, role, pageable);
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