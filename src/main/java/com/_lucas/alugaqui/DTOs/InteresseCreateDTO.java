package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para criação de um novo interesse em uma casa.")
public class InteresseCreateDTO {

    @NotNull
    @Schema(description = "ID da casa na qual o locatário tem interesse.")
    private Long casaId;

    @NotNull
    @Schema(description = "Status inicial do interesse (geralmente ATIVO).")
    private StatusInteresse status;

    public InteresseCreateDTO(Long casaId, StatusInteresse status) {
        this.casaId = casaId;
        this.status = status;
    }

    public InteresseCreateDTO() {
    }

    public Long getCasaId() {
        return casaId;
    }

    public void setCasaId(Long casaId) {
        this.casaId = casaId;
    }

    public StatusInteresse getStatus() {
        return status;
    }

    public void setStatus(StatusInteresse status) {
        this.status = status;
    }
}