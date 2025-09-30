package com._lucas.alugaqui.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta após login bem-sucedido.")
public record TokenResponseDTO(
        @Schema(description = "Token JWT de autenticação e autorização.")
        String token
) {
}