package com._lucas.alugaqui.DTOs;

import com._lucas.alugaqui.models.Aluguel.StatusAluguel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO para atualização de informações de um aluguel.")
public class AluguelUpdateDTO {

    @Schema(description = "Novo status do aluguel (ATIVO, FINALIZADO, CANCELADO).")
    private StatusAluguel status;

    @Schema(description = "Novo valor mensal do aluguel.")
    private Double valor;

    @Schema(description = "Nova data de fim do aluguel (para finalização).")
    private LocalDate dataFim;

    public AluguelUpdateDTO(StatusAluguel status, double valor, LocalDate dataFim) {
        this.status = status;
        this.valor = valor;
        this.dataFim = dataFim;
    }

    public AluguelUpdateDTO() {
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}