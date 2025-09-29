package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import jakarta.validation.constraints.NotNull;

public class InteresseCreateDTO {

    @NotNull
    private Long casaId;

    // Removido o locatarioId, ele virá do JWT

    @NotNull
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