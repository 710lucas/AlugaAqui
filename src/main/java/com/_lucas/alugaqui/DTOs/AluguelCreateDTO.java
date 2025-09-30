package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para criação de um novo aluguel por um Locador.")
public class AluguelCreateDTO {

    @NotNull
    @Schema(description = "ID da casa que está sendo alugada.")
    private Long casaId;

    @NotNull
    @Schema(description = "ID do usuário que será o locatário.")
    private Long locatarioId;

    @Schema(description = "Valor mensal do aluguel.")
    private double valor;

    @NotNull
    @Schema(description = "Status inicial do aluguel (Ex: ATIVO).")
    private StatusAluguel status;

    @NotBlank
    @Schema(description = "URL do contrato de aluguel.")
    private String contratoUrl;

    public AluguelCreateDTO(Long casaId, Long locatarioId, double valor, StatusAluguel status, String contratoUrl) {
        this.casaId = casaId;
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