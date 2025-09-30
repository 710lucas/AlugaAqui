package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AluguelCreateDTO {

    @NotNull
    private Long casaId;

    @NotNull
    private Long locatarioId;

    private double valor;

    @NotNull
    private StatusAluguel status;

    @NotBlank
    private String contratoUrl;

    public AluguelCreateDTO(Long casaId, Long locatarioId, double valor, StatusAluguel status, String contratoUrl) {
        this.casaId = casaId;
        // locadorId removido, obtido do JWT
        this.locatarioId = locatarioId;
        this.valor = valor;
        this.status = status;
        this.contratoUrl = contratoUrl;
    }

    public AluguelCreateDTO() {
    }

    public Long getCasaId() {
        return casaId;
    }

    public void setCasaId(Long casaId) {
        this.casaId = casaId;
    }

    // Removido o LocadorId, o Locador é o usuário autenticado

    public Long getLocatarioId() {
        return locatarioId;
    }

    public void setLocatarioId(Long locatarioId) {
        this.locatarioId = locatarioId;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }

    public String getContratoUrl() {
        return contratoUrl;
    }

    public void setContratoUrl(String contratoUrl) {
        this.contratoUrl = contratoUrl;
    }
}