package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para atualização do status de um interesse.")
public class InteresseUpdateDTO {

    @Schema(description = "Novo status do interesse (ATIVO ou INATIVO).")
    public StatusInteresse status;

    public InteresseUpdateDTO(StatusInteresse status) {
        this.status = status;
    }

    public InteresseUpdateDTO() {
    }

    public StatusInteresse getStatus() {
        return status;
    }

    public void setStatus(StatusInteresse status) {
        this.status = status;
    }
}
