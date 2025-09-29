package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Interesse.StatusInteresse;
import java.time.LocalDateTime;

public class InteresseResponseDTO {
    private Long id;
    private Long casaId;
    private UsuarioSimpleDTO locatario;
    private StatusInteresse status;
    private LocalDateTime createdAt;

    public InteresseResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCasaId() { return casaId; }
    public void setCasaId(Long casaId) { this.casaId = casaId; }
    public UsuarioSimpleDTO getLocatario() { return locatario; }
    public void setLocatario(UsuarioSimpleDTO locatario) { this.locatario = locatario; }
    public StatusInteresse getStatus() { return status; }
    public void setStatus(StatusInteresse status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}