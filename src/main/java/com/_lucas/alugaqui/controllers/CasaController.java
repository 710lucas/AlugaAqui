package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.CasaCreateDTO;
import com._lucas.alugaqui.DTOs.CasaResponseDTO;
import com._lucas.alugaqui.DTOs.CasaUpdateDTO;
import com._lucas.alugaqui.models.Casa.Status;
import com._lucas.alugaqui.services.CasaService;
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

@RestController
@RequestMapping("/casas")
@Tag(name = "Casas", description = "Endpoints para gerenciamento de Casas/Imóveis")
public class CasaController {

    private final CasaService casaService;

    public CasaController (CasaService casaService){
        this.casaService = casaService;
    }

    @Operation(summary = "Cria uma nova casa",
            description = "Permite que um usuário com o papel LOCADOR crie e cadastre uma nova casa para aluguel.")
    @ApiResponse(responseCode = "200", description = "Casa criada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (somente Locador)")
    @PostMapping()
    public CasaResponseDTO create (@RequestBody @Valid CasaCreateDTO createDTO, Authentication authentication){
        String locadorEmail = authentication.getName();
        return this.casaService.create(createDTO, locadorEmail);
    }

    @Operation(summary = "Busca uma casa por ID",
            description = "Retorna os detalhes de uma casa específica. Acessível a todos os usuários autenticados.")
    @ApiResponse(responseCode = "200", description = "Casa encontrada")
    @ApiResponse(responseCode = "404", description = "Casa não encontrada")
    @GetMapping("/{id}")
    public CasaResponseDTO get (@PathVariable Long id) {
        return this.casaService.get(id);
    }

    @Operation(summary = "Lista casas com filtros e paginação",
            description = "Retorna uma lista paginada de casas. Locadores veem apenas suas casas; Locatários veem todas.")
    @ApiResponse(responseCode = "200", description = "Lista de casas retornada")
    @GetMapping()
    public Page<CasaResponseDTO> getAll (
            @Parameter(description = "Filtro pelo tipo de imóvel.")
            @RequestParam(required = false) String tipo,
            @Parameter(description = "Filtro pelo número mínimo de quartos.")
            @RequestParam(required = false) Integer minQuartos,
            @Parameter(description = "Filtro pelo status da casa (DISPONIVEL, ALUGADA, INATIVA).")
            @RequestParam(required = false) Status status,
            @PageableDefault(sort = "nome", size = 10, page = 1) Pageable pageable,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return this.casaService.getAll(userEmail, tipo, minQuartos, status, pageable);
    }

    @Operation(summary = "Atualiza uma casa por ID",
            description = "Atualiza as informações da casa. Requer que o usuário seja o LOCADOR da casa.")
    @ApiResponse(responseCode = "200", description = "Casa atualizada com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (não é o locador da casa)")
    @ApiResponse(responseCode = "404", description = "Casa não encontrada")
    @PatchMapping("/{id}")
    public CasaResponseDTO update (@PathVariable Long id, @Valid @RequestBody CasaUpdateDTO updateDTO, Authentication authentication) {
        String locadorEmail = authentication.getName();
        return this.casaService.update(id, updateDTO, locadorEmail);
    }

    @Operation(summary = "Deleta uma casa por ID",
            description = "Remove uma casa do sistema. Requer que o usuário seja o LOCADOR da casa.")
    @ApiResponse(responseCode = "204", description = "Casa deletada com sucesso (sem conteúdo)")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (não é o locador da casa)")
    @ApiResponse(responseCode = "404", description = "Casa não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id, Authentication authentication) {
        String locadorEmail = authentication.getName();
        this.casaService.delete(id, locadorEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}