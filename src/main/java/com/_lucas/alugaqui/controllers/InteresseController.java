package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.InteresseCreateDTO;
import com._lucas.alugaqui.DTOs.InteresseResponseDTO;
import com._lucas.alugaqui.DTOs.InteresseUpdateDTO;
import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import com._lucas.alugaqui.services.InteresseService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/interesses")
@Tag(name = "Interesses", description = "Endpoints para gerenciamento de Interesses em Casas")
public class InteresseController {

    private final InteresseService interesseService;

    public InteresseController (InteresseService interesseService) {
        this.interesseService = interesseService;
    }

    @Operation(summary = "Cria um novo interesse",
            description = "Permite que um usuário com o papel LOCATARIO manifeste interesse em uma casa.")
    @ApiResponse(responseCode = "200", description = "Interesse criado com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (somente Locatário) ou Interesse ATIVO já existe")
    @PostMapping("/")
    public InteresseResponseDTO create (@Valid @RequestBody InteresseCreateDTO createDTO, Authentication authentication){
        String locatarioEmail = authentication.getName();
        return this.interesseService.create(createDTO, locatarioEmail);
    }

    @Operation(summary = "Busca um interesse por ID",
            description = "Retorna os detalhes de um interesse. Acesso permitido ao Locatário do interesse ou ao Locador da casa.")
    @ApiResponse(responseCode = "200", description = "Interesse encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso Negado")
    @ApiResponse(responseCode = "404", description = "Interesse não encontrado")
    @GetMapping("/{id}")
    public InteresseResponseDTO get (@PathVariable Long id) {
        return this.interesseService.get(id);
    }

    @Operation(summary = "Lista interesses com filtros e paginação",
            description = "Retorna uma lista paginada de interesses. Locatários veem seus interesses; Locadores veem interesses nas suas casas.")
    @ApiResponse(responseCode = "200", description = "Lista de interesses retornada")
    @GetMapping("/")
    public Page<InteresseResponseDTO> getAll (
            @Parameter(description = "Filtro pelo status do interesse (ATIVO, INATIVO).")
            @RequestParam(required = false) StatusInteresse status,
            @PageableDefault(sort = "createdAt", size = 10) Pageable pageable,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return this.interesseService.getAll(userEmail, status, pageable);
    }

    @Operation(summary = "Atualiza um interesse por ID",
            description = "Atualiza o status do interesse. Somente o Locador da casa pode atualizar o interesse.")
    @ApiResponse(responseCode = "200", description = "Interesse atualizado com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (somente Locador da casa)")
    @ApiResponse(responseCode = "404", description = "Interesse não encontrado")
    @PatchMapping("/{id}")
    public InteresseResponseDTO update (@PathVariable Long id, @Valid @RequestBody InteresseUpdateDTO updateDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        return this.interesseService.update(id, updateDTO, userEmail);
    }

    @Operation(summary = "Deleta um interesse por ID",
            description = "Remove um interesse do sistema. Acesso permitido ao Locatário do interesse ou ao Locador da casa.")
    @ApiResponse(responseCode = "204", description = "Interesse deletado com sucesso (sem conteúdo)")
    @ApiResponse(responseCode = "403", description = "Acesso Negado")
    @ApiResponse(responseCode = "404", description = "Interesse não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        this.interesseService.delete(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}