package com._lucas.alugaqui.controllers;

import com._lucas.alugaqui.DTOs.AluguelCreateDTO;
import com._lucas.alugaqui.DTOs.AluguelResponseDTO;
import com._lucas.alugaqui.DTOs.AluguelUpdateDTO;
import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import com._lucas.alugaqui.services.AluguelService;
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
@RequestMapping("/alugueis")
@Tag(name = "Aluguéis", description = "Endpoints para gerenciamento de Contratos de Aluguel")
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController (AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    @Operation(summary = "Cria um novo aluguel",
            description = "Permite que um LOCADOR crie um novo contrato de aluguel para uma de suas casas.")
    @ApiResponse(responseCode = "200", description = "Aluguel criado com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (somente Locador ou casa não pertence ao Locador/casa indisponível)")
    @PostMapping
    public AluguelResponseDTO create (@Valid @RequestBody AluguelCreateDTO createDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        return this.aluguelService.create(createDTO, userEmail);
    }

    @Operation(summary = "Busca um aluguel por ID",
            description = "Retorna os detalhes de um aluguel. Acesso permitido apenas para o Locador ou Locatário participantes do contrato.")
    @ApiResponse(responseCode = "200", description = "Aluguel encontrado")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (não é participante)")
    @ApiResponse(responseCode = "404", description = "Aluguel não encontrado")
    @GetMapping("/{id}")
    public AluguelResponseDTO get (@PathVariable Long id) {
        return this.aluguelService.get(id);
    }

    @Operation(summary = "Lista aluguéis com filtros e paginação",
            description = "Retorna uma lista paginada de aluguéis em que o usuário autenticado é Locador ou Locatário.")
    @ApiResponse(responseCode = "200", description = "Lista de aluguéis retornada")
    @GetMapping
    public Page<AluguelResponseDTO> getAll (
            @Parameter(description = "Filtro pelo status do aluguel (ATIVO, FINALIZADO, CANCELADO).")
            @RequestParam(required = false) StatusAluguel status,
            @PageableDefault(sort = "dataInicio", size = 10) Pageable pageable,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return this.aluguelService.getAll(userEmail, status, pageable);
    }

    @Operation(summary = "Atualiza um aluguel por ID",
            description = "Atualiza o status ou valor do aluguel. Permissão restrita ao Locador do contrato.")
    @ApiResponse(responseCode = "200", description = "Aluguel atualizado com sucesso")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (somente Locador)")
    @ApiResponse(responseCode = "404", description = "Aluguel não encontrado")
    @PatchMapping("/{id}")
    public AluguelResponseDTO update (@PathVariable Long id, @Valid @RequestBody AluguelUpdateDTO updateDTO, Authentication authentication) {
        String userEmail = authentication.getName();
        return this.aluguelService.update(id, updateDTO, userEmail);
    }

    @Operation(summary = "Deleta um aluguel por ID",
            description = "Remove um contrato de aluguel. Permissão restrita ao Locador do contrato.")
    @ApiResponse(responseCode = "204", description = "Aluguel deletado com sucesso (sem conteúdo)")
    @ApiResponse(responseCode = "403", description = "Acesso Negado (somente Locador)")
    @ApiResponse(responseCode = "404", description = "Aluguel não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        this.aluguelService.delete(id, userEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}