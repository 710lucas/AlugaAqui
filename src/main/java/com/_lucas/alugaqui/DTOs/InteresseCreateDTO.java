package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import jakarta.validation.constraints.NotNull;

public class InteresseCreateDTO {

    @NotNull
    private Long casaId;

    @NotNull
    private Long locatarioId;

    @NotNull
    private StatusInteresse status;

    public InteresseCreateDTO(Long casaId, Long locatarioId, StatusInteresse status) {
        this.casaId = casaId;
        this.locatarioId = locatarioId;
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

    public Long getLocatarioId() {
        return locatarioId;
    }

    public void setLocatarioId(Long locatarioId) {
        this.locatarioId = locatarioId;
    }

    public StatusInteresse getStatus() {
        return status;
    }

    public void setStatus(StatusInteresse status) {
        this.status = status;
    }
}
